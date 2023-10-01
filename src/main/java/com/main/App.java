package com.main;


import annotation.AnnotationPropertyMapper;
import annotation.AnnotationPropertyMappers;
import annotation.Controller;
import annotation.PathVariable;
import annotation.RequestBody;
import annotation.RequestMapping;
import annotation.RequestParam;
import annotation.WebFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.config.HttpConfig;
import com.main.task.matcher.HttpBodyAnnotationAnnotatedParameterValueMatcher;
import com.main.task.response.HttpResponseSender;
import com.main.util.AnnotationUtils;
import executor.SocketHttpTaskExecutor;
import instance.AnnotatedClassObjectRepository;
import instance.AnnotatedClassObjectRepositoryCreator;
import instance.Annotations;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import matcher.CompositedEndpointTaskMatcher;
import matcher.EndpointTaskMatcher;
import matcher.MatchedEndPoint;
import matcher.RequestMethod;
import matcher.StaticResourceEndPointTaskMatcher;
import matcher.creator.JavaMethodPathMatcherCreator2;
import matcher.creator.RequestMappedMethod;
import matcher.creator.StaticResourceEndPointCreator;
import matcher.segment.PathUrl;
import parameter.BaseParameterValueMatcher;
import parameter.CompositeMethodParameterValueMatcher;
import parameter.HttpUrlAnnotationAnnotatedParameterValueMatcher;
import parameter.MethodParameterValueMatcher;
import parameter.ParameterValueClazzConverterFactory;
import parameter.ParameterValueGetter;
import parameter.RequestParameters;
import pretask.PreTaskCreator;
import pretask.PreTaskInfo;
import pretask.PreTaskWorker;
import pretask.PreTasks;
import pretask.PreTasks.ReadOnlyPreTasks;
import response.HttpResponseHeader;
import response.HttpResponseHeaderCreator;
import task.HttpEndPointTask;
import vo.ContentType;
import vo.QueryParameters;
import static annotation.AnnotationPropertyMapper.AnnotationProperties;
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
                                                                                                    "method", (a) -> ((RequestMapping) a).method()));
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
        List<AnnotatedObjectAndProperties> webFilerAnnotatedPreTaskWorkersWithProperties = objectRepository.findObjectAndAnnotationPropertiesByClassAndAnnotatedClass(
            PreTaskWorker.class, WebFilter.class, List.of("filterName", "patterns"));
        ReadOnlyPreTasks preTasks = webFilerAnnotatedPreTaskWorkersWithProperties.stream()
            .map(webFilerAnnotatedPreTaskWorkerWithProperties -> {
                PreTaskWorker preTaskWorker = (PreTaskWorker) webFilerAnnotatedPreTaskWorkerWithProperties.getObject();
                AnnotationProperties properties = webFilerAnnotatedPreTaskWorkerWithProperties.getAnnotationProperties();

                String filterName = ((String) properties.getValue("filterName")).isBlank() ? preTaskWorker.getClass().getSimpleName() : (String) properties.getValue("filterName");
                String[] patterns = (String[]) properties.getValue("patterns");

                return Arrays.stream(patterns)
                    .map(pattern -> new PreTaskInfo(filterName, pattern, preTaskWorker))
                    .collect(Collectors.toUnmodifiableList());
            })
            .flatMap(Collection::stream)
            .map(PreTaskCreator::create)
            .reduce(PreTasks.empty(), PreTasks::add, PreTasks::merge)
            .lock();

        // 3. class 로 httpPathMatcher 를 생성.
        List<Object> controllerObjects = objectRepository.findObjectByAnnotatedClass(Controller.class).stream()
            .map(AnnotatedClassObjectRepository.AnnotatedObject::getObject)
            .collect(Collectors.toUnmodifiableList());
        List<RequestMappedMethod> requestMappedMethods = controllerObjects.stream()
            .map(RequestMappedMethodExtractor::new)
            .map(RequestMappedMethodExtractor::extract)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());

        List<EndpointTaskMatcher> javaMethodEndpointTaskMatchers = requestMappedMethods.stream()
            .map(JavaMethodPathMatcherCreator2::create)
            .collect(Collectors.toUnmodifiableList());
        StaticResourceEndPointCreator staticResourceEndPointCreator = StaticResourceEndPointCreator.from(App.class, "../../resources/main", "static");
        List<StaticResourceEndPointTaskMatcher> staticResourceEndPointTaskMatchers = staticResourceEndPointCreator.create();

        List<EndpointTaskMatcher> endpointTaskMatchers = Stream.concat(javaMethodEndpointTaskMatchers.stream(), staticResourceEndPointTaskMatchers.stream())
            .collect(Collectors.toUnmodifiableList());
        EndpointTaskMatcher endpointTaskMatcher = new CompositedEndpointTaskMatcher(endpointTaskMatchers);

        // 4. http service start.
        SocketHttpTaskExecutor socketHttpTaskExecutor = SocketHttpTaskExecutor.create(HttpConfig.INSTANCE.getPort(),
                                                                                      HttpConfig.INSTANCE.getMaxConnection(),
                                                                                      HttpConfig.INSTANCE.getWaitConnection(),
                                                                                      HttpConfig.INSTANCE.getKeepAliveTime());
        log.info("server start.");
        // 구조화 필요.
        socketHttpTaskExecutor.execute(((request, response) -> {
            List<PreTaskWorker> preTaskWorkers = preTasks.findFilterWorkers(request.getHttpRequestPath().getValue().toString());
            for (PreTaskWorker preTaskWorker : preTaskWorkers) {
                preTaskWorker.prevExecute(request, response);
            }

            RequestMethod method = RequestMethod.find(request.getHttpMethod().name());
            PathUrl requestUrl = PathUrl.from(request.getHttpRequestPath().getValue().toString());
            QueryParameters queryParameters = request.getQueryParameters();

            MatchedEndPoint matchedEndPoint = endpointTaskMatcher.match(method, requestUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));
            HttpEndPointTask httpEndPointTask = matchedEndPoint.getHttpEndPointTask();

            RequestParameters pathVariableValue = new RequestParameters(matchedEndPoint.getPathVariableValue().getValues());
            RequestParameters queryParamValues = new RequestParameters(queryParameters.getParameterMap());

            // 3. todo [annotation]
            // parameter 의 타입, 어노테이팅된 어노테이션 RequestBody, PathVariable, RequestParam 을 기준으로 request 의 값을 variable 에 매칭하고 있다.
            // 판단을 annotation 모듈의 역할로 변형하자.
            MethodParameterValueMatcher methodParameterValueMatcher = new CompositeMethodParameterValueMatcher(
                Map.of(InputStream.class, new BaseParameterValueMatcher<>(request.getBodyInputStream()),
                       RequestBody.class, new HttpBodyAnnotationAnnotatedParameterValueMatcher(request.getBodyInputStream()),
                       PathVariable.class, new HttpUrlAnnotationAnnotatedParameterValueMatcher<>(PathVariable.class, pathVariableValue),
                       RequestParam.class, new HttpUrlAnnotationAnnotatedParameterValueMatcher<>(RequestParam.class, queryParamValues))
            );

            ParameterValueGetter parameterValueGetter = new ParameterValueGetter(methodParameterValueMatcher, new ParameterValueClazzConverterFactory(new ObjectMapper()));
            Object[] parameterValues = Arrays.stream(httpEndPointTask.getExecuteParameters())
                .map(parameterValueGetter::get)
                .map(v -> v.orElse(null))
                .toArray();
            Optional<HttpEndPointTask.HttpTaskResult> optionalResult = httpEndPointTask.execute(parameterValues);

            log.info("methodResult : `{}`, clazz : `{}`", optionalResult.orElse(null), optionalResult.map(Object::getClass).orElse(null));

            // 빈값 처리 핗요함
//            if (optionalResult.isEmpty()) {
//                return true;
//            }

            HttpEndPointTask.HttpTaskResult httpTaskResult = optionalResult.get();
            ContentType contentType = httpTaskResult.getContentType();
            InputStream content = httpTaskResult.getContent();

            HttpResponseHeaderCreator headerCreator = new HttpResponseHeaderCreator(SIMPLE_DATE_FORMAT, HOST_ADDRESS, contentType);
            HttpResponseHeader httpResponseHeader = headerCreator.create();

            HttpResponseSender httpResponseSender = new HttpResponseSender(response);
            httpResponseSender.send(httpResponseHeader, content);

            // post task 들어갈 자리
//            for (PreTaskWorker preTaskWorker : preTaskWorkers) {
//                preTaskWorker.postExecute(request, response);
//            }
        }));
    }

    // 2. todo [annotation]
    // RequestMapping, Controller 어노테이션을 참조하고 있다.
    // 해당 어노테이션의 사용법을 알고 있다.
    // 시나리오 중심의 개념이다.
    // 정책적인 부분으로 변환해 보자.
    private static class RequestMappedMethodExtractor {
        private static final Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;
        private static final Class<Controller> CONTROLLER_CLASS = Controller.class;

        private final Object object;
        private final Class<?> clazz;
        private final Method[] methods;

        public RequestMappedMethodExtractor(Object object) {
            if (Objects.isNull(object)) {
                throw new RuntimeException("object is emtpy.");
            }

            Class<?> objectClass = object.getClass();
            boolean doesNotAnnotatedControllerAnnotation = AnnotationUtils.doesNotExist(objectClass, CONTROLLER_CLASS);
            if (doesNotAnnotatedControllerAnnotation) {
                throw new RuntimeException("requestMapped method is emtpy.");
            }

            Method[] methods = AnnotationUtils.peekMethods(objectClass, REQUEST_MAPPING_CLASS).toArray(Method[]::new);
            if (methods.length == 0) {
                throw new RuntimeException("requestMapped method is emtpy.");
            }

            this.object = object;
            this.clazz = objectClass;
            this.methods = methods;
        }

        public List<RequestMappedMethod> extract() {
            return Arrays.stream(methods)
                .map(method -> doExtract(this.object, this.clazz, method))
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
        }

        // [input] annotation, [output] (1:N class:Methods  reuestMapping, RequestMapping) => N개
        // class - method ->
        private static List<RequestMappedMethod> doExtract(Object object, Class<?> clazz, Method javaMethod) {
            Optional<RequestMapping> clazzRequestMapping = AnnotationUtils.find(clazz, REQUEST_MAPPING_CLASS);
            RequestMapping methodRequestMapping = AnnotationUtils.find(javaMethod, REQUEST_MAPPING_CLASS)
                .orElseThrow(() -> new RuntimeException("method does not have RequestMapping."));

            List<RequestMethod> requestMethods = Arrays.stream(methodRequestMapping.method()).collect(Collectors.toUnmodifiableList());
            List<String> clazzUrls = clazzRequestMapping
                .map(c -> Arrays.asList(c.url()))
                .orElseGet(Collections::emptyList);
            List<String> methodUrls = Arrays.stream(methodRequestMapping.url())
                .collect(Collectors.toUnmodifiableList());

            List<String> fullMethodUrls = clazzUrls.stream()
                .flatMap(clazzUrl -> methodUrls.stream()
                    .map(methodUrl -> clazzUrl + methodUrl))
                .collect(Collectors.toUnmodifiableList());

            return requestMethods.stream()
                .flatMap(httpMethod -> fullMethodUrls.stream()
                    .map(methodUrl -> new RequestMappedMethod(httpMethod, methodUrl, object, javaMethod)))
                .collect(Collectors.toUnmodifiableList());
        }
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