package com.main;


import annotation.AnnotationPropertyMapper;
import annotation.AnnotationPropertyMappers;
import annotation.Controller;
import annotation.PathVariable;
import annotation.RequestBody;
import annotation.RequestMapping;
import annotation.RequestParam;
import annotation.PreWebFilter;
import com.main.config.HttpConfig;
import com.main.task.executor.EndPointTaskExecutor;
import com.main.task.response.HttpResponseSender;
import com.main.util.AnnotationUtils;
import converter.CompositeValueTypeConverter;
import executor.SocketHttpTaskExecutor;
import instance.AnnotatedClassObjectRepository;
import instance.AnnotatedClassObjectRepositoryCreator;
import instance.AnnotatedMethodProperties;
import instance.AnnotatedObjectAndMethodProperties;
import instance.AnnotatedObjectProperties;
import instance.AnnotationProperties;
import instance.AnnotationPropertyGetter;
import instance.Annotations;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import matcher.RequestMethod;
import matcher.creator.JavaMethodInvokeTaskWorkerCreator2;
import matcher.creator.EndPointMethodInfo;
import matcher.segment.PathUrl;
import parameter.UrlParameterValues;
import parameter.extractor.HttpBodyParameterInfoExtractor.HttpBodyParameterInfo;
import parameter.matcher.HttpBodyParameterValueAssignee;
import parameter.matcher.HttpUrlParameterValueAssignee;
import parameter.matcher.ParameterAndValueAssigneeType;
import parameter.matcher.ParameterValueAssignee;
import parameter.matcher.ParameterValueAssignees2;
import pretask.PreTaskCreator;
import pretask.PreTaskInfo;
import pretask.PreTaskWorker;
import pretask.PreTasks;
import pretask.PreTasks.ReadOnlyPreTasks;
import response.HttpResponseHeader;
import response.HttpResponseHeaderCreator2;
import task.BaseEndPointTask2;
import task.CompositedEndpointTasks;
import task.EndPointTask2;
import task.ResourceEndPointFindTask2;
import task.SystemResourceFinder;
import task.worker.EndPointWorkerResult;
import task.worker.JavaMethodInvokeTaskWorker2;
import task.worker.WorkerResultType;
import vo.ContentType2;
import static parameter.extractor.HttpUrlParameterInfoExtractor.HttpUrlParameterInfo;
import static parameter.matcher.ParameterValueAssigneeType.BODY;
import static parameter.matcher.ParameterValueAssigneeType.INPUT_STREAM;
import static parameter.matcher.ParameterValueAssigneeType.OUTPUT_STREAM;
import static parameter.matcher.ParameterValueAssigneeType.QUERY_PARAM;
import static parameter.matcher.ParameterValueAssigneeType.URL;
import static task.worker.WorkerResultType.EMPTY;
import static vo.ContentType2.APPLICATION_JAVASCRIPT;
import static vo.ContentType2.APPLICATION_JAVA_VM;
import static vo.ContentType2.APPLICATION_JSON;
import static vo.ContentType2.IMAGE_GIF;
import static vo.ContentType2.IMAGE_JPEG;
import static vo.ContentType2.IMAGE_PNG;
import static vo.ContentType2.TEXT_CSS;
import static vo.ContentType2.TEXT_HTML;
import static vo.ContentType2.TEXT_PLAIN;

@Slf4j
public class App2 {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final String HOST_ADDRESS;
    private static final AnnotationPropertyMappers ANNOTATION_PROPERTY_MAPPERS;
    private static final CompositeValueTypeConverter VALUE_TYPE_CONVERTER = new CompositeValueTypeConverter();

    static {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HOST_ADDRESS = getHostAddress();

        AnnotationPropertyMapper webFilterPropertyMapper = new AnnotationPropertyMapper(PreWebFilter.class,
                                                                                        Map.of("patterns", (a) -> ((PreWebFilter) a).patterns(),
                                                                                               "filterName", (a) -> ((PreWebFilter) a).filterName()));
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
        ANNOTATION_PROPERTY_MAPPERS = new AnnotationPropertyMappers(Map.of(PreWebFilter.class, webFilterPropertyMapper,
                                                                           PathVariable.class, pathVariablePropertyMapper,
                                                                           RequestBody.class, requestBodyPropertyMapper,
                                                                           RequestMapping.class, requestMappingPropertyMapper,
                                                                           RequestParam.class, requestParamPropertyMapper));
    }

    public static void main(String[] args) {
        // 1. annotating 된 class 의 instance 를 생성한다.
        AnnotationPropertyGetter annotationPropertyGetter = new AnnotationPropertyGetter(ANNOTATION_PROPERTY_MAPPERS);
        AnnotatedClassObjectRepositoryCreator objectRepositoryCreator = AnnotatedClassObjectRepositoryCreator
            .builderWithDefaultAnnotations()
            .annotations(new Annotations(List.of(PreWebFilter.class, Controller.class)))
            .annotationPropertyGetter(annotationPropertyGetter)
            .build();
        AnnotatedClassObjectRepository objectRepository = objectRepositoryCreator.fromPackage(App2.class, "com.main");

        // 2. webfilter 생성.
        List<AnnotatedObjectProperties> webFilerAnnotatedPreTaskWorkersWithProperties = objectRepository.findObjectAndAnnotationPropertiesByClassAndAnnotatedClass(PreTaskWorker.class,
                                                                                                                                                                   PreWebFilter.class,
                                                                                                                                                                   List.of("filterName", "patterns"));
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
        List<AnnotatedObjectAndMethodProperties> requestMappedProperties = objectRepository.findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatedClassFocusOnMethod(controllerAnnotatedClasses,
                                                                                                                                                                        RequestMapping.class,
                                                                                                                                                                        List.of("url", "httpMethod"));
        List<EndPointMethodInfo> endPointJavaMethodInfos = requestMappedProperties.stream()
            .map(requestMappedProperty -> {
                AnnotatedObjectProperties annotatedObjectProperties = requestMappedProperty.getAnnotatedObjectProperties();
                Object object = annotatedObjectProperties.getObject();
                AnnotationProperties objectProperties = annotatedObjectProperties.getAnnotationProperties();

                AnnotatedMethodProperties annotatedMethodProperties = requestMappedProperty.getAnnotatedMethodProperties();
                Method javaMethod = annotatedMethodProperties.getJavaMethod();
                AnnotationProperties methodProperties = annotatedMethodProperties.getAnnotationProperties();

                RequestMethod[] httpMethods = (RequestMethod[]) methodProperties.getValueOrDefault("httpMethod", new RequestMethod[]{});
                String[] classUrls = (String[]) objectProperties.getValueOrDefault("url", Collections.emptyList());
                String[] methodUrls = (String[]) methodProperties.getValueOrDefault("url", Collections.emptyList());
                return createEndPointMethodInfos(httpMethods, classUrls, methodUrls, object, javaMethod);
            })
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());

        // 4. endPointTask create.
        JavaMethodInvokeTaskWorkerCreator2 javaMethodInvokeTaskWorkerCreator2 = new JavaMethodInvokeTaskWorkerCreator2(parameterParameterAndValueAssigneeTypeFunction());
        List<EndPointTask2> endPointTasks = endPointJavaMethodInfos.stream()
            .map(endPointJavaMethodInfo -> {
                RequestMethod requestMethod = endPointJavaMethodInfo.getRequestMethod();
                String url = endPointJavaMethodInfo.getUrl();
                Object object = endPointJavaMethodInfo.getObject();
                Method javaMethod = endPointJavaMethodInfo.getJavaMethod();

                JavaMethodInvokeTaskWorker2 taskWorker = javaMethodInvokeTaskWorkerCreator2.create(object, javaMethod);

                return BaseEndPointTask2.from(requestMethod, url, taskWorker);
            })
            .collect(Collectors.toUnmodifiableList());
        // 5. static resource find task.
        SystemResourceFinder systemResourceFinder = SystemResourceFinder.fromPackage(App.class, "../../resources/main");
        ResourceEndPointFindTask2 resourceEndPointFindTask2 = new ResourceEndPointFindTask2(systemResourceFinder, "/static");

        // 6. combine each endpointTask.
        endPointTasks = Stream.concat(endPointTasks.stream(), Stream.of(resourceEndPointFindTask2)).collect(Collectors.toUnmodifiableList());
        CompositedEndpointTasks compositedEndpointTasks = new CompositedEndpointTasks(endPointTasks);

        // 7. execute service.
        SocketHttpTaskExecutor socketHttpTaskExecutor = SocketHttpTaskExecutor.create(HttpConfig.INSTANCE.getPort(),
                                                                                      HttpConfig.INSTANCE.getMaxConnection(),
                                                                                      HttpConfig.INSTANCE.getWaitConnection(),
                                                                                      HttpConfig.INSTANCE.getKeepAliveTime());
        log.info("server start.");
        socketHttpTaskExecutor.execute(((request, response) -> {
            preTasks.execute(request, response);

            InputStream bodyInputStream = request.getBodyInputStream();
            UrlParameterValues queryParamValues = new UrlParameterValues(request.getQueryParameters().getParameterMap());
            Function<UrlParameterValues, ParameterValueAssignees2> urlParameterValuesParameterValueAssignees2Function = createUrlParameterValuesParameterValueAssignees2Function(
                (pathParameters) -> new HttpUrlParameterValueAssignee(pathVariableHttpUrlParameterInfoFunction(annotationPropertyGetter), pathParameters),
                new HttpUrlParameterValueAssignee(requestParamHttpUrlParameterInfoFunction(annotationPropertyGetter), queryParamValues),
                new HttpBodyParameterValueAssignee(requestBodyHttpUrlParameterInfoFunction(annotationPropertyGetter), bodyInputStream)
            );
            EndPointTaskExecutor endPointTaskExecutor = new EndPointTaskExecutor(urlParameterValuesParameterValueAssignees2Function, compositedEndpointTasks);

            RequestMethod method = RequestMethod.find(request.getHttpMethod().name());
            PathUrl requestUrl = PathUrl.from(request.getHttpRequestPath().getValue().toString());
            EndPointWorkerResult endPointWorkerResult = endPointTaskExecutor.execute(method, requestUrl);

            WorkerResultType type = endPointWorkerResult.getType();
            Object result = endPointWorkerResult.getResult();
            InputStream content = VALUE_TYPE_CONVERTER.convertToInputStream(result);
            ContentType2 contentType = getContentType2(type);

            HttpResponseHeaderCreator2 headerCreator = new HttpResponseHeaderCreator2(SIMPLE_DATE_FORMAT, HOST_ADDRESS, contentType);
            HttpResponseHeader httpResponseHeader = headerCreator.create();
            HttpResponseSender httpResponseSender = new HttpResponseSender(response);
            httpResponseSender.send(httpResponseHeader, content);
        }));
    }

    private static ContentType2 getContentType2(WorkerResultType type) {
        if (type == EMPTY) {
            return null;
        }
        return convertToContentType(type);
    }

    private static Function<UrlParameterValues, ParameterValueAssignees2> createUrlParameterValuesParameterValueAssignees2Function(
        Function<UrlParameterValues, ParameterValueAssignee> urlParameterValuesPathParametersValueAssigneeFunction,
        HttpUrlParameterValueAssignee requestParamValueAssignee,
        HttpBodyParameterValueAssignee bodyParameterValueAssignee
    ) {
        return (pathVariableValue) -> new ParameterValueAssignees2(
            Map.of(
                URL, urlParameterValuesPathParametersValueAssigneeFunction.apply(pathVariableValue),
                QUERY_PARAM, requestParamValueAssignee,
                BODY, bodyParameterValueAssignee
            ));
    }

    private static List<PreTaskInfo> createPreTaskInfos(PreTaskWorker preTaskWorker, String filterName, String[] patterns) {
        return Arrays.stream(patterns)
            .map(pattern -> new PreTaskInfo(filterName, pattern, preTaskWorker))
            .collect(Collectors.toUnmodifiableList());
    }

    private static List<EndPointMethodInfo> createEndPointMethodInfos(RequestMethod[] requestMethods, String[] classUrls, String[] _methodUrls, Object object, Method javaMethod) {
        List<String> clazzUrls = Arrays.stream(classUrls).collect(Collectors.toUnmodifiableList());
        List<String> methodUrls = Arrays.stream(_methodUrls).collect(Collectors.toUnmodifiableList());
        List<String> fullMethodUrls = clazzUrls.stream()
            .flatMap(clazzUrl -> methodUrls.stream()
                .map(methodUrl -> clazzUrl + methodUrl))
            .collect(Collectors.toUnmodifiableList());

        return Arrays.stream(requestMethods)
            .flatMap(httpMethod -> fullMethodUrls.stream()
                .map(methodUrl -> new EndPointMethodInfo(httpMethod, methodUrl, object, javaMethod)))
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

    private static Function<Parameter, HttpUrlParameterInfo> requestParamHttpUrlParameterInfoFunction(AnnotationPropertyGetter annotationPropertyGetter) {
        Objects.requireNonNull(annotationPropertyGetter);
        return parameter -> {
            AnnotationProperties annotationProperties = annotationPropertyGetter.getAnnotationProperties(parameter, RequestParam.class, List.of("name", "defaultValue", "required"));

            String parameterName = (String) annotationProperties.getValueOrDefault("name", parameter.getName());
            String defaultValue = (String) annotationProperties.getValue("defaultValue");
            boolean required = (boolean) annotationProperties.getValue("required");

            return new HttpUrlParameterInfo(parameterName, defaultValue, required);
        };
    }

    private static Function<Parameter, HttpUrlParameterInfo> pathVariableHttpUrlParameterInfoFunction(AnnotationPropertyGetter annotationPropertyGetter) {
        Objects.requireNonNull(annotationPropertyGetter);
        return parameter -> {
            AnnotationProperties annotationProperties = annotationPropertyGetter.getAnnotationProperties(parameter, PathVariable.class, List.of("name", "required"));

            String parameterName = (String) annotationProperties.getValueOrDefault("name", parameter.getName());
            boolean required = (boolean) annotationProperties.getValue("required");
            String defaultValue = null;

            return new HttpUrlParameterInfo(parameterName, defaultValue, required);
        };
    }

    private static Function<Parameter, HttpBodyParameterInfo> requestBodyHttpUrlParameterInfoFunction(AnnotationPropertyGetter annotationPropertyGetter) {
        Objects.requireNonNull(annotationPropertyGetter);
        return parameter -> {
            AnnotationProperties annotationProperties = annotationPropertyGetter.getAnnotationProperties(parameter, RequestBody.class, List.of("required"));

            boolean required = (boolean) annotationProperties.getValue("required");

            return new HttpBodyParameterInfo(required);
        };
    }

    // annotation util 이 흘러나온다.
    public static Function<Parameter, ParameterAndValueAssigneeType> parameterParameterAndValueAssigneeTypeFunction() {
        return parameter -> {
            Class<?> parameterType = parameter.getType();

            // 1. pure parameter type.
            if (InputStream.class.isAssignableFrom(parameterType)) {
                return new ParameterAndValueAssigneeType(parameter, INPUT_STREAM);
            }
            if (OutputStream.class.isAssignableFrom(parameterType)) {
                return new ParameterAndValueAssigneeType(parameter, OUTPUT_STREAM);
            }

            // 2. annotation hint type.
            if (AnnotationUtils.exist(parameter, PathVariable.class)) {
                return new ParameterAndValueAssigneeType(parameter, URL);
            }
            if (AnnotationUtils.exist(parameter, RequestParam.class)) {
                return new ParameterAndValueAssigneeType(parameter, QUERY_PARAM);
            }
            if (AnnotationUtils.exist(parameter, RequestBody.class)) {
                return new ParameterAndValueAssigneeType(parameter, BODY);
            }

            throw new RuntimeException("Does not exist possible match ParameterType.");
        };
    }

    private static ContentType2 convertToContentType(WorkerResultType resultType) {
        switch (resultType) {
            case STRING:
            case TXT:
                return TEXT_PLAIN;
            case HTML:
                return TEXT_HTML;
            case CSS:
                return TEXT_CSS;
            case JPEG:
                return IMAGE_JPEG;
            case GIF:
                return IMAGE_GIF;
            case PNG:
                return IMAGE_PNG;
            case JSON:
                return APPLICATION_JSON;
            case JAVASCRIPT:
                return APPLICATION_JAVASCRIPT;
            case CLASS:
                return APPLICATION_JAVA_VM;
        }

        throw new RuntimeException(MessageFormat.format("Does not exit match type. WorkerResultType: `{}`", resultType));
    }
}


