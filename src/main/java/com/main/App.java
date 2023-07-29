package com.main;


import com.main.util.AnnotationUtils;
import container.ClassFinder;
import container.ComponentClassInitializer;
import container.ObjectRepository;
import container.annotation.Component;
import container.annotation.Controller;
import filter.Filter;
import filter.FilterWorker;
import filter.Filters;
import filter.annotation.WebFilter;
import filter.chain.FilterChain;
import filter.chain.FilterWorkerChain;
import filter.chain.HttpRequestProcessorChain;
import filter.pattern.PatternMatcher;
import filter.pattern.PatternMatcherStrategy;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import matcher.BaseEndpointJavaMethodMatcher;
import matcher.BaseEndpointJavaMethodMatcher.MatchedMethod;
import matcher.CompositedEndpointJavaMethodMatcher;
import matcher.EndpointJavaMethodMatcher;
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
import matcher.converter.base.CompositeConverter;
import matcher.creator.JavaMethodPathMatcherCreator;
import matcher.segment.PathUrl;
import processor.HttpRequestProcessor;
import processor.HttpService;
import vo.HttpRequest;
import vo.HttpRequestReader;
import vo.HttpResponse;
import vo.HttpResponseWriter;
import vo.QueryParameters;

@Slf4j
public class App {
    private static final Class<Component> COMPONENT_CLASS = Component.class;
    private static final Class<Controller> CONTROLLER_CLASS = Controller.class;
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;
    private static final Objects EMPTY_VALUE = null;

    public static void main(String[] args) {
        // 1. class 를 모두 찾아옴.
        List<Class<?>> clazzes = ClassFinder.from(App.class, "com.main").findClazzes();
        log.info("clazzes : {}", clazzes);

        // 2. class 로 container 를 생성.
        List<Class<?>> componentClazzes = AnnotationUtils.filterByAnnotatedClazz(clazzes, COMPONENT_CLASS);
        List<ComponentClassInitializer> componentClassInitializers = componentClazzes.stream()
            .map(ComponentClassInitializer::new)
            .collect(Collectors.toUnmodifiableList());
        ObjectRepository objectRepository = createContainer(componentClassInitializers);

        // 3. class 로 httpPathMatcher 를 생성.
        List<Class<?>> controllerClazzes = AnnotationUtils.filterByAnnotatedClazz(clazzes, CONTROLLER_CLASS);
        List<BaseEndpointJavaMethodMatcher> baseHttpPathMatchers = controllerClazzes.stream()
            .map(JavaMethodPathMatcherCreator::new)
            .map(JavaMethodPathMatcherCreator::create)
            .flatMap(Collection::stream)
            .peek(httpPathMatcher -> log.info("httpPathMatcher : `{}`", httpPathMatcher))
            .collect(Collectors.toUnmodifiableList());
        EndpointJavaMethodMatcher endpointJavaMethodMatcher = new CompositedEndpointJavaMethodMatcher(baseHttpPathMatchers);

        // 4. class 로 webfilter 를 생성.
        List<Class<?>> webFilterAnnotatedClazzes = AnnotationUtils.filterByAnnotatedClazz(clazzes, WEB_FILTER_CLASS);
        log.info("webFilterAnnotatedClazzes : {}", webFilterAnnotatedClazzes);
        List<Filter> filters = webFilterAnnotatedClazzes.stream()
            .map(webFilterAnnotatedClazz -> createFilters(objectRepository, webFilterAnnotatedClazz))
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
        Filters newFilters = new Filters(filters);

        log.info("newFilters : {}", newFilters);

        BaseHttpRequestExecutor baseHttpRequestExecutor = new BaseHttpRequestExecutor(objectRepository, endpointJavaMethodMatcher);
        HttpService httpService = new HttpService(baseHttpRequestExecutor, newFilters);
        httpService.start();
    }

    private static ObjectRepository createContainer(List<ComponentClassInitializer> componentClassInitializers) {
        ObjectRepository objectRepository = ObjectRepository.empty();
        for (ComponentClassInitializer componentClassInitializer : componentClassInitializers) {
            ObjectRepository newObjectRepository = componentClassInitializer.load(objectRepository);
            objectRepository = objectRepository.merge(newObjectRepository);
        }
        return objectRepository;
    }

    public static List<Filter> createFilters(ObjectRepository objectRepository, Class<?> filterWorkerClazz) {
        Objects.requireNonNull(filterWorkerClazz);
        if (util.AnnotationUtils.doesNotExist(filterWorkerClazz, WEB_FILTER_CLASS)) {
            throw new RuntimeException("does not exist component annotation");
        }

        Class<?>[] memberClasses = util.AnnotationUtils.peekFieldsType(filterWorkerClazz, COMPONENT_CLASS).toArray(Class<?>[]::new);
        Object[] memberObjects = Arrays.stream(memberClasses).map(objectRepository::get).toArray(Object[]::new);
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

    public static class BaseHttpRequestExecutor implements HttpRequestProcessor {
        private static final CompositeConverter converter = new CompositeConverter();

        private final ObjectRepository objectRepository;
        private final EndpointJavaMethodMatcher endpointJavaMethodMatcher;

        public BaseHttpRequestExecutor(ObjectRepository objectRepository, EndpointJavaMethodMatcher endpointJavaMethodMatcher) {
            this.objectRepository = objectRepository;
            this.endpointJavaMethodMatcher = endpointJavaMethodMatcher;
        }


        @Override
        public boolean execute(HttpRequest request, HttpResponse response) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(response);

            RequestMethod method = RequestMethod.find(request.getHttpMethod().name());
            PathUrl requestUrl = PathUrl.from(request.getHttpRequestPath().getValue().toString());
            QueryParameters queryParameters = request.getQueryParameters();
            BodyContent bodyContent = BodyContent.from(request.getBodyInputStream());

            MatchedMethod matchedMethod = endpointJavaMethodMatcher.match(method, requestUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));
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
            Object instance = objectRepository.get(declaringClass);
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

    public static class RequestRunner implements processor.RequestRunner {
        private final HttpRequestProcessor httpRequestProcessor;
        private final Filters filters;

        public RequestRunner(HttpRequestProcessor httpRequestProcessor, Filters filters) {
            Objects.requireNonNull(httpRequestProcessor);
            Objects.requireNonNull(filters);
            this.httpRequestProcessor = httpRequestProcessor;
            this.filters = filters;
        }

        @Override
        public void run(InputStream inputStream, OutputStream outputStream) {
            HttpRequestReader httpRequestReader = new HttpRequestReader(inputStream);
            HttpRequest httpRequest = httpRequestReader.read();
            HttpResponse httpResponse = new HttpResponse(outputStream);

            log.info(httpRequest.getHttpRequestPath().toString());

            List<FilterWorker> filterWorkers = filters.findFilterWorkers(httpRequest.getHttpRequestPath().toString());
            log.info("filterWorkers : {}", filterWorkers);

            log.info("create filter chain");
            FilterChain applicationExecutorChain = new HttpRequestProcessorChain(httpRequestProcessor, null);
            FilterChain filterChain = filterWorkers.stream()
                .reduce(
                    applicationExecutorChain,
                    FilterWorkerChain::new,
                    (pw, pw2) -> null);

            log.info("execute filter chain");
            filterChain.execute(httpRequest, httpResponse);
        }
    }
}