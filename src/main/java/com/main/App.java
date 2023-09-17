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
import filter.WebFilterAnnotatedFilterCreator;
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

        // 3. webfilter 생성.
        List<FilterWorker> filterWorkerObjects = objectRepository.findObjectByClazz(FilterWorker.class);
        Filters filters = filterWorkerObjects.stream()
            .map(WebFilterAnnotatedFilterCreator::new)
            .map(WebFilterAnnotatedFilterCreator::create)
            .reduce(Filters.empty(),Filters::merge);

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

        BaseHttpRequestProcessor baseHttpRequestProcessor = new BaseHttpRequestProcessor(endpointTaskMatcher, SIMPLE_DATE_FORMAT, HOST_ADDRESS);
        HttpService httpService = HttpService.from(baseHttpRequestProcessor, filters,
                                                   HttpConfig.INSTANCE.getPort(),
                                                   HttpConfig.INSTANCE.getMaxConnection(),
                                                   HttpConfig.INSTANCE.getWaitConnection(),
                                                   HttpConfig.INSTANCE.getKeepAliveTime());
        httpService.start();
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