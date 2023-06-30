package com.main;


import com.main.extractor.ParameterValueExtractor;
import com.main.extractor.ParameterValueExtractorStrategy;
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
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
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
import matcher.converter.BodyContent;
import matcher.converter.RequestParameters;
import matcher.converter.base.CompositeConverter;
import matcher.converter.base.ObjectConverter;
import matcher.creator.JavaMethodPathMatcherCreator;
import matcher.segment.PathUrl;
import processor.HttpRequestExecutor;
import processor.HttpService;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.HttpResponseWriter;

@Slf4j
public class App {
    private static final Class<Component> COMPONENT_CLASS = Component.class;
    private static final Class<Controller> CONTROLLER_CLASS = Controller.class;
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;
    private static final Objects EMPTY_VALUE = null;

    private static final ObjectConverter objectConverter = new ObjectConverter();

    public static void main(String[] args) {
        // [시스템 컴포넌트적 요소 존재.]
        // 1. class 를 모두 찾아옴.
        List<Class<?>> clazzes = ClassFinder.from(App.class, "com.main.business").findClazzes();

        // 2. class 로 container 를 생성.
        List<Class<?>> componentClazzes = AnnotationUtils.filterByAnnotatedClazz(clazzes, COMPONENT_CLASS);
        List<ComponentClassLoader> componentClassLoaders = componentClazzes.stream()
            .map(ComponentClassLoader::new)
            .collect(Collectors.toUnmodifiableList());
        Container container = createContainer(componentClassLoaders);

        // 3. class 로 httpPathMatcher 를 생성.
        List<Class<?>> controllerClazzes = AnnotationUtils.filterByAnnotatedClazz(clazzes, CONTROLLER_CLASS);
        List<BaseHttpPathMatcher> baseHttpPathMatchers = controllerClazzes.stream()
            .map(JavaMethodPathMatcherCreator::new)
            .map(JavaMethodPathMatcherCreator::create)
            .flatMap(Collection::stream)
            .peek(httpPathMatcher -> log.info("httpPathMatcher : `{}`", httpPathMatcher))
            .collect(Collectors.toUnmodifiableList());
        HttpPathMatcher httpPathMatcher = new CompositedHttpPathMatcher(baseHttpPathMatchers);

        // 4. class 로 webfilter 를 생성.
        List<Class<?>> webFilterAnnotatedClazzes = AnnotationUtils.filterByAnnotatedClazz(clazzes, WEB_FILTER_CLASS);
        List<Filter> filters = webFilterAnnotatedClazzes.stream()
            .map(webFilterAnnotatedClazz -> createFilters(container, webFilterAnnotatedClazz))
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
        Filters newFilters = new Filters(filters);

        BaseRequestExecutor baseRequestExecutor = new BaseRequestExecutor(container, httpPathMatcher);
        HttpService httpService = new HttpService(baseRequestExecutor, newFilters);
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

    public static class BaseRequestExecutor implements HttpRequestExecutor {
        private static final CompositeConverter converter = new CompositeConverter();

        private final Container container;
        private final HttpPathMatcher httpPathMatcher;

        public BaseRequestExecutor(Container container, HttpPathMatcher httpPathMatcher) {
            this.container = container;
            this.httpPathMatcher = httpPathMatcher;
        }

        @Override
        public boolean execute(HttpRequest request, HttpResponse response) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(response);

            // 5. http data 가져오기. (does not need compoenet)
            String methodName = request.getHttpMethod().name();
            String url = request.getHttpUri().getUrl();

            // 6.match method 찾기
            RequestMethod method = RequestMethod.find(methodName);
            PathUrl pathUrl = PathUrl.from(url);
            MatchedMethod matchedMethod = httpPathMatcher.matchJavaMethod(method, pathUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));

            // todo, 값 변환자 생성이 아닌 값 변환이 필요하다.
            // [시스템 컴포넌트적 요소 존재.]
            // 7.
            // as is.
            // (1) 값 변환자 생성.
            // to be.
            // (1) 값 변환.
            RequestParameters pathVariableValue = new RequestParameters(matchedMethod.getPathVariableValue().getValues());
            RequestParameters queryParamValues = new RequestParameters(request.getQueryParameters().getParameterMap());
            BodyContent bodyContent = BodyContent.from(request.getBodyInputStream());
            ParameterValueExtractorStrategy parameterValueExtractorStrategy = new ParameterValueExtractorStrategy(pathVariableValue, queryParamValues, bodyContent);

            Method javaMethod = matchedMethod.getJavaMethod();

            // [시스템 컴포넌트적 요소 존재.]
            // 8.method 실행.
            // (1) class, instance 가져오기.
            Class<?> declaringClass = javaMethod.getDeclaringClass();
            Object instance = container.get(declaringClass);
            log.info("declaringClass : {}", declaringClass);
            log.info("instance : {}", instance);
            log.info("javaMethod : {}", javaMethod);

            // -> todo, parameter 에서 값 변환은 사전에 진행되었어야한다.
            // 8.1.
            // as is. (1) parameter -> value 변환.
            // to be. (1) nothing.
            Object[] values = Arrays.stream(javaMethod.getParameters())
                .map(parameterValueExtractorStrategy::create)
                .map(ParameterValueExtractor::extract)
                .map(extractValue -> {
                    String value = extractValue.getOptionalValue().orElse("");
                    Class<?> parameterType = extractValue.getParameterType();
                    return objectConverter.convert(value, parameterType);
                })
                .peek(value -> log.info("value : {}, {}", value, value.getClass()))
                .toArray();

            // 8.2. 실행.
            Object result = doExecute(instance, javaMethod, values);

            // [시스템 컴포넌트적 요소 존재.]
            // 9. 응답값 생성.
            // as is.
            // (1) result -> to input stream.
            // (2) 임의의 header 셋팅
            // to be.
            // (1) 응닶 타입에 따라 content-type 설정.
            InputStream inputStream = converter.convertToInputStream(result);
            response.setStartLine("HTTP/1.1 200 OK");
            response.appendHeader(Map.of(
                "Date", "MON, 27 Jul 2023 12:28:53 GMT",
                "Host", "localhost:8080",
                "Content-Type", "text/html; charset=UTF-8"));
            HttpResponseWriter sender = response.getSender();
            sender.send(inputStream);

            return true;
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