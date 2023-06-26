package com.main;


import com.main.executor.ApplicationRequestExecutor;
import com.main.executor.ApplicationRequestExecutor2;
import com.main.executor.MethodExecutor;
import com.main.filter.WebFilterComponentFilterCreator;
import com.main.util.AnnotationUtils;
import container.ClassFinder;
import container.ComponentClassLoader;
import container.Container;
import container.annotation.Component;
import container.annotation.Controller;
import filter.Filters;
import filter.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import matcher.*;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestBody;
import matcher.annotation.RequestParam;
import matcher.converter.*;
import matcher.converter.base.CompositeConverter;
import matcher.creator.JavaMethodPathMatcherCreator;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import processor.HttpService;
import vo.QueryParameters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.main.util.AnnotationUtils.*;
import static com.main.util.AnnotationUtils.exist;

@Slf4j
public class App {
    private static final Class<Component> COMPONENT_CLASS = Component.class;
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;
    private static final Class<Controller> CONTROLLER_CLASS = Controller.class;

    public static void main(String[] args) {
        // 1. class 를 모두 찾아옴.
        List<Class<?>> clazzes = ClassFinder.from(App.class, "com.main.business").findClazzes();

        // 2. class 로 container 를 생성.
        List<Class<?>> componentClazzes = clazzes.stream()
                .filter(clazz -> !Objects.isNull(clazz))
                .filter(clazz -> exist(clazz, COMPONENT_CLASS))
                .collect(Collectors.toUnmodifiableList());
        List<ComponentClassLoader> componentClassLoaders = componentClazzes.stream()
                .map(ComponentClassLoader::new)
                .collect(Collectors.toUnmodifiableList());
        Container container = componentClassLoaders.stream()
                .reduce(Container.empty(),
                        (container1, componentClassLoader) -> componentClassLoader.load(container1),
                        Container::merge);

        // 3. class 로 webfilter 를 생성.
        List<Class<?>> webFilterAnnotatedClazzes = clazzes.stream()
                .filter(clazz -> !Objects.isNull(clazz))
                .filter(clazz -> exist(clazz, WEB_FILTER_CLASS))
                .collect(Collectors.toUnmodifiableList());
        WebFilterComponentFilterCreator webFilterComponentFilterCreator = new WebFilterComponentFilterCreator(container);
        Filters filters = webFilterAnnotatedClazzes.stream()
                .map(webFilterComponentFilterCreator::create)
                .reduce(Filters.empty(), Filters::merge);

        // 4. class 로 httpPathMatcher 를 생성.
        List<Class<?>> controllerClazzes = clazzes.stream()
                .filter(clazz -> !Objects.isNull(clazz))
                .filter(clazz -> exist(clazz, CONTROLLER_CLASS))
                .collect(Collectors.toUnmodifiableList());
        List<BaseHttpPathMatcher> baseHttpPathMatchers = controllerClazzes.stream()
                .map(JavaMethodPathMatcherCreator::new)
                .map(JavaMethodPathMatcherCreator::create)
                .flatMap(Collection::stream)
                .peek(httpPathMatcher -> log.info("httpPathMatcher : `{}`", httpPathMatcher))
                .collect(Collectors.toUnmodifiableList());
        HttpPathMatcher httpPathMatcher = new CompositedHttpPathMatcher(baseHttpPathMatchers);

        MethodExecutor methodExecutor1 = new MethodExecutor(container);
        RequestExecutor methodExecutor = new RequestExecutor(httpPathMatcher, methodExecutor1);
        CompositeConverter converter = new CompositeConverter();
        ApplicationRequestExecutor2 applicationRequestExecutor = new ApplicationRequestExecutor2(methodExecutor, converter);

        HttpService httpService = new HttpService(applicationRequestExecutor, filters);
        httpService.start();
    }

    public static class RequestExecutor {
        private final HttpPathMatcher httpPathMatcher;
        private final MethodExecutor methodExecutor;

        public RequestExecutor(HttpPathMatcher httpPathMatcher, MethodExecutor methodExecutor) {
            this.httpPathMatcher = httpPathMatcher;
            this.methodExecutor = methodExecutor;
        }

        public Object doExecute(RequestMethod method, String requestUrl, QueryParameters queryParameters, BodyContent bodyContent) {
            PathUrl requestPathUrl = PathUrl.from(requestUrl);
            BaseHttpPathMatcher.MatchedMethod matchedMethod = httpPathMatcher.matchJavaMethod(method, requestPathUrl)
                    .orElseThrow(() -> new RuntimeException(""));

            Method javaMethod = matchedMethod.getJavaMethod();
            PathVariableValue pathVariableValue = matchedMethod.getPathVariableValue();
            RequestParameters queryParamValues = new RequestParameters(queryParameters.getParameterMap());

            Map<Class<? extends Annotation>, ParameterConverter> classParameterConverterMap = Map.of(
                    RequestParam.class, new RequestParameterConverter(RequestParam.class, queryParamValues),
                    PathVariable.class, RequestParameterConverter.from(PathVariable.class, pathVariableValue),
                    RequestBody.class, new RequestBodyParameterConverter(bodyContent)
            );
            CompositeParameterConverter compositeParameterConverter = new CompositeParameterConverter(classParameterConverterMap);

            Optional<Object> result = methodExecutor.execute(javaMethod, compositeParameterConverter);

            if (result.isEmpty()) {
                return "empty";
            }

            return result.get();
        }
    }
}