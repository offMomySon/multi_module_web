package com.main;


import com.main.task.BaseHttpRequestProcessor;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
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
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final String HOST_ADDRESS;
    static {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HOST_ADDRESS = getHostAddress();
    }

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

        BaseHttpRequestProcessor baseHttpRequestProcessor = new BaseHttpRequestProcessor(objectRepository, endpointJavaMethodMatcher, SIMPLE_DATE_FORMAT, HOST_ADDRESS);
        HttpService httpService = new HttpService(baseHttpRequestProcessor, newFilters);
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

    private static String getHostAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}