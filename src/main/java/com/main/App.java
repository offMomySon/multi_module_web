package com.main;


import annotation.AnnotationPropertyMapper;
import annotation.AnnotationPropertyMappers;
import annotation.Controller;
import annotation.PathVariable;
import annotation.RequestBody;
import annotation.RequestMapping;
import annotation.RequestParam;
import annotation.WebFilter;
import com.main.config.HttpConfig;
import com.main.task.executor.BaseHttpRequestProcessor;
import executor.SocketHttpTaskExecutor;
import instance.AnnotatedClassObjectRepository;
import instance.AnnotatedClassObjectRepositoryCreator;
import instance.Annotations;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import matcher.CompositedEndpointTaskMatcher;
import matcher.EndpointTaskMatcher;
import matcher.RequestMethod;
import matcher.StaticResourceEndPointTaskMatcher;
import matcher.creator.JavaMethodPathMatcherCreator;
import matcher.creator.RequestMappedMethod;
import matcher.creator.StaticResourceEndPointCreator;
import pretask.PreTaskCreator;
import pretask.PreTaskInfo;
import pretask.PreTaskWorker;
import pretask.PreTasks;
import pretask.PreTasks.ReadOnlyPreTasks;
import static annotation.AnnotationPropertyMapper.AnnotationProperties;
import static instance.AnnotatedClassObjectRepository.AnnotatedMethodAndProperties;
import static instance.AnnotatedClassObjectRepository.AnnotatedObjectAndMethodProperties;
import static instance.AnnotatedClassObjectRepository.AnnotatedObjectAndProperties;

@Slf4j
public class App {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final String HOST_ADDRESS;
    private static final AnnotationPropertyMappers ANNOTATION_PROPERTY_MAPPERS;

    static {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HOST_ADDRESS = getHostAddress();
        AnnotationPropertyMapper webFilterPropertyMapper = new AnnotationPropertyMapper(WebFilter.class,
                                                                                        Map.of("patterns", (a) -> ((WebFilter) a).patterns(),
                                                                                               "filterName", (a) -> ((WebFilter) a).filterName()));
        AnnotationPropertyMapper pathVariablePropertyMapper = new AnnotationPropertyMapper(PathVariable.class,
                                                                                           Map.of("name", (a) -> ((PathVariable) a).name(),
                                                                                                  "required", (a) -> ((PathVariable) a).required()));
        AnnotationPropertyMapper requestBodyPropertyMapper = new AnnotationPropertyMapper(RequestBody.class,
                                                                                          Map.of("required", (a) -> ((RequestBody) a).required()));

        AnnotationPropertyMapper requestMappingPropertyMapper = new AnnotationPropertyMapper(RequestMapping.class,
                                                                                             Map.of("url", (a) -> ((RequestMapping) a).url(),
                                                                                                    "httpMethod", (a) -> ((RequestMapping) a).method()));
        AnnotationPropertyMapper requestParamPropertyMapper = new AnnotationPropertyMapper(RequestParam.class,
                                                                                           Map.of("name", (a) -> ((RequestParam) a).name(),
                                                                                                  "defaultValue", (a) -> ((RequestParam) a).defaultValue(),
                                                                                                  "required", (a) -> ((RequestParam) a).required()));
        ANNOTATION_PROPERTY_MAPPERS = new AnnotationPropertyMappers(Map.of(WebFilter.class, webFilterPropertyMapper,
                                                                           PathVariable.class, pathVariablePropertyMapper,
                                                                           RequestBody.class, requestBodyPropertyMapper,
                                                                           RequestMapping.class, requestMappingPropertyMapper,
                                                                           RequestParam.class, requestParamPropertyMapper));
    }

    public static void main(String[] args) {
        // 1. annotating 된 class 의 instance 를 생성한다.
        AnnotatedClassObjectRepositoryCreator objectRepositoryCreator = AnnotatedClassObjectRepositoryCreator
            .builderWithDefaultAnnotations()
            .appendAnnotations(new Annotations(List.of(WebFilter.class, Controller.class)))
            .appendAnnotationPropertyMappers(ANNOTATION_PROPERTY_MAPPERS)
            .build();
        AnnotatedClassObjectRepository objectRepository = objectRepositoryCreator.createFromPackage(App.class, "com.main");

        // 2. webfilter 생성.
        List<AnnotatedObjectAndProperties> webFilerAnnotatedPreTaskWorkersWithProperties = objectRepository.findObjectAndAnnotationPropertiesByClassAndAnnotatedClass(PreTaskWorker.class,
                                                                                                                                                                      WebFilter.class,
                                                                                                                                                                      List.of("filterName",
                                                                                                                                                                              "patterns"));
        ReadOnlyPreTasks preTasks = webFilerAnnotatedPreTaskWorkersWithProperties.stream()
            .map(webFilerAnnotatedPreTaskWorkerWithProperties -> {
                PreTaskWorker preTaskWorker = (PreTaskWorker) webFilerAnnotatedPreTaskWorkerWithProperties.getObject();
                AnnotationProperties properties = webFilerAnnotatedPreTaskWorkerWithProperties.getAnnotationProperties();

                String filterName = ((String) properties.getValue("filterName")).isBlank() ? preTaskWorker.getClass().getSimpleName() : (String) properties.getValue("filterName");
                String[] patterns = (String[]) properties.getValue("patterns");
                return createPreTaskInfos(preTaskWorker, filterName, patterns);
            })
            .flatMap(Collection::stream)
            .map(PreTaskCreator::create)
            .reduce(PreTasks.empty(), PreTasks::add, PreTasks::merge)
            .lock();

        // 3. java http endpoint task 생성.
        List<Class<?>> controllerAnnotatedClasses = objectRepository.findClassByAnnotatedClass(Controller.class);
        List<AnnotatedObjectAndMethodProperties> requestMappedProperties = objectRepository.findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatdClassAtMethodBase(controllerAnnotatedClasses,
                                                                                                                                                                      RequestMapping.class,
                                                                                                                                                                      List.of("url", "httpMethod"));

        List<RequestMappedMethod> requestMappedMethods = requestMappedProperties.stream()
            .map(requestMappedProperty -> {
                AnnotatedObjectAndProperties annotatedObjectAndProperties = requestMappedProperty.getAnnotatedObjectAndProperties();
                Object object = annotatedObjectAndProperties.getObject();
                AnnotationProperties objectProperties = annotatedObjectAndProperties.getAnnotationProperties();

                AnnotatedMethodAndProperties annotatedMethodAndProperties = requestMappedProperty.getAnnotatedMethodAndProperties();
                Method javaMethod = annotatedMethodAndProperties.getJavaMethod();
                AnnotationProperties methodProperties = annotatedMethodAndProperties.getAnnotationProperties();

                RequestMethod[] httpMethods = (RequestMethod[]) methodProperties.getValueOrDefault("httpMethod", new RequestMethod[]{});
                String[] classUrls = (String[]) objectProperties.getValueOrDefault("url", Collections.emptyList());
                String[] _methodUrls = (String[]) methodProperties.getValueOrDefault("url", Collections.emptyList());
                return createRequestMappedMethods(httpMethods, classUrls, _methodUrls, object, javaMethod);
            })
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());

        List<EndpointTaskMatcher> javaMethodEndpointTaskMatchers = requestMappedMethods.stream()
            .map(JavaMethodPathMatcherCreator::create)
            .collect(Collectors.toUnmodifiableList());

        // 4. resource http endpoint task 생성.
        StaticResourceEndPointCreator staticResourceEndPointCreator = StaticResourceEndPointCreator.from(App.class, "../../resources/main", "static");
        List<StaticResourceEndPointTaskMatcher> staticResourceEndPointTaskMatchers = staticResourceEndPointCreator.create();

        List<EndpointTaskMatcher> endpointTaskMatchers = Stream.concat(javaMethodEndpointTaskMatchers.stream(), staticResourceEndPointTaskMatchers.stream()).collect(Collectors.toUnmodifiableList());
        EndpointTaskMatcher endpointTaskMatcher = new CompositedEndpointTaskMatcher(endpointTaskMatchers);

        // 5. http service start.
        SocketHttpTaskExecutor socketHttpTaskExecutor = SocketHttpTaskExecutor.create(HttpConfig.INSTANCE.getPort(),
                                                                                      HttpConfig.INSTANCE.getMaxConnection(),
                                                                                      HttpConfig.INSTANCE.getWaitConnection(),
                                                                                      HttpConfig.INSTANCE.getKeepAliveTime());
        BaseHttpRequestProcessor baseHttpRequestProcessor = new BaseHttpRequestProcessor(endpointTaskMatcher, SIMPLE_DATE_FORMAT, HOST_ADDRESS);
        log.info("server start.");
        // 구조화 필요.
        socketHttpTaskExecutor.execute(((request, response) -> {
            List<PreTaskWorker> preTaskWorkers = preTasks.findFilterWorkers(request.getHttpRequestPath().getValue().toString());
            for (PreTaskWorker preTaskWorker : preTaskWorkers) {
                preTaskWorker.prevExecute(request, response);
            }

            baseHttpRequestProcessor.execute(request, response);

            // todo
            // post task.
        }));
    }

    private static List<PreTaskInfo> createPreTaskInfos(PreTaskWorker preTaskWorker, String filterName, String[] patterns) {
        return Arrays.stream(patterns)
            .map(pattern -> new PreTaskInfo(filterName, pattern, preTaskWorker))
            .collect(Collectors.toUnmodifiableList());
    }

    private static List<RequestMappedMethod> createRequestMappedMethods(RequestMethod[] requestMethods, String[] classUrls, String[] _methodUrls, Object object, Method javaMethod) {
        List<String> clazzUrls = Arrays.stream(classUrls).collect(Collectors.toUnmodifiableList());
        List<String> methodUrls = Arrays.stream(_methodUrls).collect(Collectors.toUnmodifiableList());
        List<String> fullMethodUrls = clazzUrls.stream()
            .flatMap(clazzUrl -> methodUrls.stream()
                .map(methodUrl -> clazzUrl + methodUrl))
            .collect(Collectors.toUnmodifiableList());

        return Arrays.stream(requestMethods)
            .flatMap(httpMethod -> fullMethodUrls.stream()
                .map(methodUrl -> new RequestMappedMethod(httpMethod, methodUrl, object, javaMethod)))
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