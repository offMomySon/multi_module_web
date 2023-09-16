package com.main;


import com.main.config.HttpConfig;
import com.main.task.BaseHttpRequestProcessor;
import com.main.util.AnnotationUtils;
import com.main.util.ClassFinder;
import annotation.Controller;
import filter.Filter;
import filter.FilterWorker;
import filter.Filters;
import annotation.WebFilter;
import filter.pattern.PatternMatcher;
import filter.pattern.PatternMatcherStrategy;
import instance.AnnotatedClassObjectRepositoryCreator;
import instance.Annotations;
import instance.ReadOnlyObjectRepository;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import matcher.CompositedEndpointTaskMatcher;
import matcher.EndpointTaskMatcher;
import matcher.StaticResourceEndPointCreator;
import matcher.StaticResourceEndPointTaskMatcher;
import matcher.creator.JavaMethodPathMatcherCreator;
import processor.HttpService;

@Slf4j
public class App {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final String HOST_ADDRESS;

    static {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HOST_ADDRESS = getHostAddress();
    }

    public static void main(String[] args) {
        // 1. class 를 모두 찾아옴.
        List<Class<?>> clazzes = ClassFinder.from(App.class, "com.main").findClazzes();
        log.info("clazzes : {}", clazzes);

        // 2. class 로 container 를 생성.
        Annotations customAnnotations = new Annotations(List.of(WebFilter.class, Controller.class));
        AnnotatedClassObjectRepositoryCreator objectRepositoryCreator = AnnotatedClassObjectRepositoryCreator.registCustomAnnotations(customAnnotations);
        ReadOnlyObjectRepository objectRepository = objectRepositoryCreator.create(clazzes);

        // 3. class 로 httpPathMatcher 를 생성.
        List<Class<?>> controllerClazzes = AnnotationUtils.filterByAnnotatedClazz(clazzes, Controller.class);
        List<EndpointTaskMatcher> baseHttpPathMatchers = controllerClazzes.stream()
            .map(clazz -> new JavaMethodPathMatcherCreator(clazz, objectRepository))
            .map(JavaMethodPathMatcherCreator::create)
            .flatMap(Collection::stream)
            .peek(httpPathMatcher -> log.info("EndpointTaskMatcher : `{}`", httpPathMatcher))
            .collect(Collectors.toUnmodifiableList());
        StaticResourceEndPointCreator staticResourceEndPointCreator = StaticResourceEndPointCreator.from(App.class, "../../resources/main", "static");
        List<StaticResourceEndPointTaskMatcher> staticResourceEndPointJavaMethodMatchers = staticResourceEndPointCreator.create();
        List<EndpointTaskMatcher> endpointTaskMatchers = Stream.concat(baseHttpPathMatchers.stream(), staticResourceEndPointJavaMethodMatchers.stream())
            .collect(Collectors.toUnmodifiableList());
        EndpointTaskMatcher endpointTaskMatcher = new CompositedEndpointTaskMatcher(endpointTaskMatchers);

        // 4. class 로 webfilter 를 생성.
//        List<Class<?>> webFilterAnnotatedClazzes = AnnotationUtils.filterByAnnotatedClazz(clazzes, WEB_FILTER_CLASS);
//        log.info("webFilterAnnotatedClazzes : {}", webFilterAnnotatedClazzes);
//        List<Filter> filters = webFilterAnnotatedClazzes.stream()
//            .map(webFilterAnnotatedClazz -> createFilters(objectRepository, webFilterAnnotatedClazz))
//            .flatMap(Collection::stream)
//            .collect(Collectors.toUnmodifiableList());
//        Filters newFilters = new Filters(filters);
//        log.info("newFilters : {}", newFilters);

        BaseHttpRequestProcessor baseHttpRequestProcessor = new BaseHttpRequestProcessor(endpointTaskMatcher, SIMPLE_DATE_FORMAT, HOST_ADDRESS);
        HttpService httpService = HttpService.from(baseHttpRequestProcessor, new Filters(Collections.emptyList()),
                                                   HttpConfig.INSTANCE.getPort(),
                                                   HttpConfig.INSTANCE.getMaxConnection(),
                                                   HttpConfig.INSTANCE.getWaitConnection(),
                                                   HttpConfig.INSTANCE.getKeepAliveTime());
        httpService.start();
    }

//    public static List<Filter> createFilters(ObjectRepository objectRepository, Class<?> filterWorkerClazz) {
//        Objects.requireNonNull(filterWorkerClazz);
//        if (util.AnnotationUtils.doesNotExist(filterWorkerClazz, WEB_FILTER_CLASS)) {
//            throw new RuntimeException("does not exist component annotation");
//        }
//
//        Class<?>[] memberClasses = util.AnnotationUtils.peekFieldsType(filterWorkerClazz, COMPONENT_CLASS).toArray(Class<?>[]::new);
//        Object[] memberObjects = Arrays.stream(memberClasses).map(objectRepository::get).toArray(Object[]::new);
//        FilterWorker filterWorker = (FilterWorker) newObject(filterWorkerClazz, memberClasses, memberObjects);
//
//        WebFilter webFilter = AnnotationUtils.find(filterWorkerClazz, WEB_FILTER_CLASS).orElseThrow(() -> new RuntimeException("filter does not annotated WebFilter."));
//        String filterName = webFilter.filterName().isEmpty() ? filterWorker.getClass().getSimpleName() : webFilter.filterName();
//        List<String> basePaths = Arrays.stream(webFilter.patterns()).collect(Collectors.toUnmodifiableList());
//
//        return basePaths.stream()
//            .map(basePath -> createFilter(filterName, basePath, filterWorker))
//            .collect(Collectors.toUnmodifiableList());
//    }

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