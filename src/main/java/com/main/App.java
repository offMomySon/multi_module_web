package com.main;


import com.main.executor.ApplicationRequestExecutor2;
import com.main.util.AnnotationUtils;
import container.ClassFinder;
import container.ComponentClassLoader;
import container.Container;
import container.annotation.Component;
import container.annotation.Controller;
import filter.Filter;
import filter.FilterWorker;
import filter.Filters;
import filter.annotation.WebFilter;
import filter.pattern.PatternMatcher;
import filter.pattern.PatternMatcherStrategy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import matcher.BaseHttpPathMatcher;
import matcher.BaseHttpPathMatcher.MatchedMethod;
import matcher.CompositedHttpPathMatcher;
import matcher.HttpPathMatcher;
import matcher.RequestMethod;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestBody;
import matcher.annotation.RequestParam;
import matcher.converter.BodyContent;
import matcher.converter.CompositeParameterConverter;
import matcher.converter.ParameterConverter;
import matcher.converter.RequestBodyParameterConverter;
import matcher.converter.RequestParameterConverter;
import matcher.converter.RequestParameters;
import matcher.creator.JavaMethodPathMatcherCreator;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import processor.HttpService;
import vo.QueryParameters;
import static com.main.util.AnnotationUtils.exist;

@Slf4j
public class App {
    private static final Class<Component> COMPONENT_CLASS = Component.class;
    private static final Class<Controller> CONTROLLER_CLASS = Controller.class;
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;
    private static final Objects EMPTY_VALUE = null;

    public static void main(String[] args) {
        // 1. class 를 모두 찾아옴.
        List<Class<?>> clazzes = ClassFinder.from(App.class, "com.main.business").findClazzes();

        // 2. class 로 container 를 생성.
        List<Class<?>> componentClazzes = AnnotationUtils.filterByAnnotationClazz(clazzes, COMPONENT_CLASS);
        List<ComponentClassLoader> componentClassLoaders = componentClazzes.stream()
            .map(ComponentClassLoader::new)
            .collect(Collectors.toUnmodifiableList());
        Container container = createContainer(componentClassLoaders);

        // 3. class 로 httpPathMatcher 를 생성.
        List<Class<?>> controllerClazzes = AnnotationUtils.filterByAnnotationClazz(clazzes, CONTROLLER_CLASS);
        List<BaseHttpPathMatcher> baseHttpPathMatchers = controllerClazzes.stream()
            .map(JavaMethodPathMatcherCreator::new)
            .map(JavaMethodPathMatcherCreator::create)
            .flatMap(Collection::stream)
            .peek(httpPathMatcher -> log.info("httpPathMatcher : `{}`", httpPathMatcher))
            .collect(Collectors.toUnmodifiableList());
        HttpPathMatcher httpPathMatcher = new CompositedHttpPathMatcher(baseHttpPathMatchers);

        // 4. class 로 webfilter 를 생성.
        List<Class<?>> webFilterAnnotatedClazzes = AnnotationUtils.filterByAnnotationClazz(clazzes, WEB_FILTER_CLASS);
        List<Filter> filters = webFilterAnnotatedClazzes.stream()
            .map(webFilterAnnotatedClazz -> createFilters(container, webFilterAnnotatedClazz))
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
        Filters newFilters = new Filters(filters);

        RequestExecutor methodExecutor = new RequestExecutor(container, httpPathMatcher);
        ApplicationRequestExecutor2 applicationRequestExecutor = new ApplicationRequestExecutor2(methodExecutor);

        HttpService httpService = new HttpService(applicationRequestExecutor, newFilters);
        httpService.start();
    }

    private static Container createContainer(List<ComponentClassLoader> componentClassLoaders) {
        Container container = Container.empty();
        for (ComponentClassLoader classLoader : componentClassLoaders) {
            Container newContainer = classLoader.load(container);
            container = container.merge(newContainer);
        }
        return container;
    }

    public static List<Filter> createFilters(Container container, Class<?> filterWorkerClazz) {
        Objects.requireNonNull(filterWorkerClazz);
        if (AnnotationUtils.doesNotExist(filterWorkerClazz, WEB_FILTER_CLASS)) {
            throw new RuntimeException("does not exist component annotation");
        }

        Class<?>[] memberClasses = AnnotationUtils.peekFieldsType(filterWorkerClazz, COMPONENT_CLASS).toArray(Class<?>[]::new);
        Object[] memberObjects = Arrays.stream(memberClasses).map(container::get).toArray(Object[]::new);
        FilterWorker filterWorker = (FilterWorker) newObject(filterWorkerClazz, memberClasses, memberObjects);

        WebFilter webFilter = AnnotationUtils.find(filterWorkerClazz, WEB_FILTER_CLASS).orElseThrow(() -> new RuntimeException("filter does not annotated WebFilter."));
        String filterName = webFilter.filterName().isEmpty() ? filterWorker.getClass().getSimpleName() : webFilter.filterName();
        List<String> basePaths = Arrays.stream(webFilter.patterns()).collect(Collectors.toUnmodifiableList());

        return basePaths.stream()
            .map(basePath -> createFilter(filterName, basePath, filterWorker))
            .collect(Collectors.toUnmodifiableList());
    }

    private static Object newObject(Class<?> filterWorkerClazz, Class<?>[] memberClasses, Object[] memberObjects) {
        try {
            Constructor<?> constructor = filterWorkerClazz.getConstructor(memberClasses);
            return constructor.newInstance(memberObjects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Filter createFilter(String filterName, String basePath, FilterWorker filterWorker) {
        PatternMatcher patternMatcher = PatternMatcherStrategy.create(basePath);
        return new Filter(filterName, patternMatcher, filterWorker);
    }

    public static class RequestExecutor {
        private final Container container;
        private final HttpPathMatcher httpPathMatcher;

        public RequestExecutor(Container container, HttpPathMatcher httpPathMatcher) {
            this.container = container;
            this.httpPathMatcher = httpPathMatcher;
        }

        public Object execute(RequestMethod method, PathUrl requestUrl, QueryParameters queryParameters, BodyContent bodyContent) {
            MatchedMethod matchedMethod = httpPathMatcher.matchJavaMethod(method, requestUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));

            Method javaMethod = matchedMethod.getJavaMethod();
            RequestParameters pathVariableValue = new RequestParameters(matchedMethod.getPathVariableValue().getValues());
            RequestParameters queryParamValues = new RequestParameters(queryParameters.getParameterMap());

            Map<Class<? extends Annotation>, ParameterConverter> parameterConverters = Map.of(
                PathVariable.class, new RequestParameterConverter(PathVariable.class, pathVariableValue),
                RequestParam.class, new RequestParameterConverter(RequestParam.class, queryParamValues),
                RequestBody.class, new RequestBodyParameterConverter(bodyContent)
            );
            ParameterConverter parameterConverter = new CompositeParameterConverter(parameterConverters);

            Class<?> declaringClass = javaMethod.getDeclaringClass();
            Object instance = container.get(declaringClass);
            log.info("declaringClass : {}", declaringClass);
            log.info("instance : {}", instance);
            log.info("javaMethod : {}", javaMethod);

            Object[] values = Arrays.stream(javaMethod.getParameters())
                .peek(parameter -> log.info("parameter : `{}`, param class : `{}`", parameter, parameter.getClass()))
                .map(parameterConverter::convertAsValue)
                .map(optionalValue -> optionalValue.orElse(EMPTY_VALUE))
                .peek(value -> log.info("value : {}, {}", value, value.getClass()))
                .toArray();

            Object result = doExecute(instance, javaMethod, values);
            return Objects.isNull(result) ? "emtpy" : result;
        }

        private static Object doExecute(Object object, Method javaMethod, Object[] paramsValues) {
            try {
                log.info("object : {}, javaMethod : {}, paramsValues : {}", object.getClass(), javaMethod, paramsValues);
                return javaMethod.invoke(object, paramsValues);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}