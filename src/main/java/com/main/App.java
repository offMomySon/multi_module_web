package com.main;


import annotation.Controller;
import annotation.WebFilter;
import com.main.config.HttpConfig;
import com.main.task.executor.BaseHttpRequestProcessor;
import com.main.util.AnnotationUtils;
import executor.SocketHttpTaskExecutor;
import filter.PreTaskWorker;
import filter.PreTasks;
import filter.PreTasks.ReadOnlyPreTasks;
import instance.AnnotatedClassObjectRepositoryCreator;
import instance.Annotations;
import instance.ReadOnlyObjectRepository;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import matcher.CompositedEndpointTaskMatcher;
import matcher.EndpointTaskMatcher;
import matcher.StaticResourceEndPointTaskMatcher;
import matcher.creator.JavaMethodPathMatcherCreator;
import matcher.creator.StaticResourceEndPointCreator;
import pretask.PreTaskCreator;
import pretask.PreTaskInfo;

@Slf4j
public class App {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final String HOST_ADDRESS;

    static {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HOST_ADDRESS = getHostAddress();
    }

    public static void main(String[] args) {
        // 1. 등록된 annotation 이 마킹된 class 들에 대해 instance 를 생성한다.
        // todo
        // 만약 같은 모듈이면 괜찮은가?
        // List<Class<?>> clazzes = ClassFinder.from(rootClazz, classPackage).findClazzes();
        Annotations customAnnotations = new Annotations(List.of(WebFilter.class, Controller.class));
        AnnotatedClassObjectRepositoryCreator objectRepositoryCreator = AnnotatedClassObjectRepositoryCreator.registCustomAnnotations(customAnnotations);
        ReadOnlyObjectRepository objectRepository = objectRepositoryCreator.createFromPackage(App.class, "com.main");

        // 2. webfilter 생성.
        List<PreTaskWorker> preTaskWorkerObjects = objectRepository.findObjectByClazz(PreTaskWorker.class);
        ReadOnlyPreTasks preTasks = preTaskWorkerObjects.stream()
            .map(App::extractPreTaskInfos)
            .flatMap(Collection::stream)
            .map(PreTaskCreator::create)
            .reduce(PreTasks.empty(), PreTasks::add, PreTasks::merge)
            .lock();

        // 3. class 로 httpPathMatcher 를 생성.
        List<Object> controllerObjects = objectRepository.findAnnotatedObjectFrom(Controller.class);
        List<EndpointTaskMatcher> baseHttpPathMatchers = controllerObjects.stream()
            .map(JavaMethodPathMatcherCreator::new)
            .map(JavaMethodPathMatcherCreator::create)
            .flatMap(Collection::stream)
            .peek(httpPathMatcher -> log.info("EndpointTaskMatcher : `{}`", httpPathMatcher))
            .collect(Collectors.toUnmodifiableList());
        StaticResourceEndPointCreator staticResourceEndPointCreator = StaticResourceEndPointCreator.from(App.class, "../../resources/main", "static");
        List<StaticResourceEndPointTaskMatcher> staticResourceEndPointJavaMethodMatchers = staticResourceEndPointCreator.create();
        List<EndpointTaskMatcher> endpointTaskMatchers = Stream.concat(baseHttpPathMatchers.stream(), staticResourceEndPointJavaMethodMatchers.stream())
            .collect(Collectors.toUnmodifiableList());
        EndpointTaskMatcher endpointTaskMatcher = new CompositedEndpointTaskMatcher(endpointTaskMatchers);

        // 4. http service start.
        BaseHttpRequestProcessor baseHttpRequestProcessor = new BaseHttpRequestProcessor(endpointTaskMatcher, SIMPLE_DATE_FORMAT, HOST_ADDRESS);
        SocketHttpTaskExecutor socketHttpTaskExecutor = SocketHttpTaskExecutor.create(HttpConfig.INSTANCE.getPort(),
                                                                                      HttpConfig.INSTANCE.getMaxConnection(),
                                                                                      HttpConfig.INSTANCE.getWaitConnection(),
                                                                                      HttpConfig.INSTANCE.getKeepAliveTime());
        socketHttpTaskExecutor.execute(((httpRequest, httpResponse) -> {
            List<PreTaskWorker> preTaskWorkers = preTasks.findFilterWorkers(httpRequest.getHttpRequestPath().getValue().toString());
            for (PreTaskWorker preTaskWorker : preTaskWorkers) {
                preTaskWorker.prevExecute(httpRequest, httpResponse);
            }
            baseHttpRequestProcessor.execute(httpRequest, httpResponse);
            for (PreTaskWorker preTaskWorker : preTaskWorkers) {
                preTaskWorker.postExecute(httpRequest, httpResponse);
            }
        }));
    }

    private static List<PreTaskInfo> extractPreTaskInfos(PreTaskWorker preTaskWorker) {
        if (Objects.isNull(preTaskWorker)) {
            throw new RuntimeException("filterWorker is emtpy.");
        }

        Class<? extends PreTaskWorker> filterWorkerClass = preTaskWorker.getClass();
        WebFilter webFilter = AnnotationUtils.find(filterWorkerClass, WebFilter.class)
            .orElseThrow(() -> new RuntimeException("For create Filter, FilterWorker must exist WebFilter annotation."));

        String name = webFilter.filterName().isEmpty() ? preTaskWorker.getClass().getSimpleName() : webFilter.filterName();
        String[] patterns = webFilter.patterns();

        return Arrays.stream(patterns)
            .map(p -> new PreTaskInfo(name, p, preTaskWorker))
            .collect(Collectors.toUnmodifiableList());
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