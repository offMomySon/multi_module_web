package com.main;


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
import instance.AnnotatedClassObjectRepositoryCreator;
import instance.Annotations;
import instance.ReadOnlyObjectRepository;
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
import static instance.ReadOnlyObjectRepository.*;

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
        List<Object> controllerObjects1 = objectRepository.findObjectByAnnotatedClass(Controller.class).stream()
            .map(AnnotatedObject::getObject)
            .collect(Collectors.toUnmodifiableList());
        List<RequestMappedMethod> requestMappedMethods = controllerObjects1.stream()
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

    // 1. todo [annotation]
    // WebFilter 어노테이션을 참조하고 있다.
    // 해당 어노테이션의 사용법을 알고 있다.
    // 시나리오 중심의 개념이다.
    // 정책적인 부분으로 변환해 보자.
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
                .map(c -> Arrays.asList(c.value()))
                .orElseGet(Collections::emptyList);
            List<String> methodUrls = Arrays.stream(methodRequestMapping.value())
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