package com.main;


import annotation.AnnotationPropertyMapper;
import annotation.AnnotationPropertyMappers;
import annotation.Component;
import annotation.Controller;
import annotation.Domain;
import annotation.PathVariable;
import annotation.PostWebFilter;
import annotation.PreWebFilter;
import annotation.Repository;
import annotation.RequestBody;
import annotation.RequestMapping;
import annotation.RequestParam;
import annotation.Service;
import com.main.util.AnnotationUtils;
import converter.CompositeValueTypeConverter;
import executor.SocketHttpTaskExecutor;
import instance.AnnotatedMethodProperties;
import instance.AnnotatedObjectAndMethodProperties;
import instance.AnnotatedObjectProperties;
import instance.AnnotatedObjectRepository;
import instance.AnnotatedObjectRepositoryCreator;
import instance.AnnotationProperties;
import instance.AnnotationPropertyGetter;
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
import lombok.extern.slf4j.Slf4j;
import matcher.PathMatcher;
import matcher.RequestMethod;
import matcher.creator.EndPointMethodInfo;
import matcher.path.PathUrl;
import parameter.extractor.HttpBodyParameterInfoExtractor.HttpBodyParameterInfo;
import parameter.matcher.ParameterAndValueAssigneeType;
import pretask.PostTaskInfo;
import pretask.PreTaskInfo;
import task.PostTaskWorker;
import task.PreTaskWorker;
import task.worker.WorkerResultType;
import vo.ContentType2;
import static com.main.config.HttpConfig.INSTANCE;
import static matcher.PathMatcher.MatchedElement;
import static matcher.PathMatcher.Token;
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
public class App4 {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final String RESOURCE_PREFIX = "/static";
    private static final String HOST_ADDRESS;
    private static final AnnotationPropertyMappers ANNOTATION_PROPERTY_MAPPERS;
    private static final CompositeValueTypeConverter VALUE_TYPE_CONVERTER = new CompositeValueTypeConverter();

    static {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HOST_ADDRESS = getHostAddress();

        AnnotationPropertyMapper preWebFilterPropertyMapper = new AnnotationPropertyMapper(PreWebFilter.class,
                                                                                           Map.of("patterns", (a) -> ((PreWebFilter) a).patterns(),
                                                                                                  "filterName", (a) -> ((PreWebFilter) a).filterName()));
        AnnotationPropertyMapper postWebFilterPropertyMapper = new AnnotationPropertyMapper(PostWebFilter.class,
                                                                                            Map.of("patterns", (a) -> ((PostWebFilter) a).patterns(),
                                                                                                   "filterName", (a) -> ((PostWebFilter) a).filterName()));
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
        ANNOTATION_PROPERTY_MAPPERS = new AnnotationPropertyMappers(Map.of(PreWebFilter.class, preWebFilterPropertyMapper,
                                                                           PostWebFilter.class, postWebFilterPropertyMapper,
                                                                           PathVariable.class, pathVariablePropertyMapper,
                                                                           RequestBody.class, requestBodyPropertyMapper,
                                                                           RequestMapping.class, requestMappingPropertyMapper,
                                                                           RequestParam.class, requestParamPropertyMapper));
    }

    public static void main(String[] args) {
//        // 1. annotating 된 class 의 instance 를 생성한다.
//        AnnotatedObjectRepositoryCreator repositoryCreator = AnnotatedObjectRepositoryCreator
//            .builder()
//            .annotations(PreWebFilter.class,
//                         Controller.class,
//                         Component.class,
//                         Domain.class,
//                         Repository.class,
//                         Service.class)
//            .build();
//        AnnotatedObjectRepository objectRepository = repositoryCreator.creaet(App4.class, "com.main");
//
//        // 4. java http endpoint task 생성.
//        List<Class<?>> controllerAnnotatedClasses = objectRepository.findClassByAnnotatedClass(Controller.class);
//        List<AnnotatedObjectAndMethodProperties> requestMappedProperties =
//            objectRepository.findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatedClassFocusOnMethod(controllerAnnotatedClasses, RequestMapping.class, List.of("url", "httpMethod"));
//
//        List<EndPointMethodInfo> endPointJavaMethodInfos = requestMappedProperties.stream()
//            .map(requestMappedProperty -> {
//                AnnotatedObjectProperties annotatedObjectProperties = requestMappedProperty.getAnnotatedObjectProperties();
//                AnnotationProperties objectProperties = annotatedObjectProperties.getAnnotationProperties();
//
//                AnnotatedMethodProperties annotatedMethodProperties = requestMappedProperty.getAnnotatedMethodProperties();
//                AnnotationProperties methodProperties = annotatedMethodProperties.getAnnotationProperties();
//
//                RequestMethod[] httpMethods = (RequestMethod[]) methodProperties.getValueOrDefault("httpMethod", new RequestMethod[]{});
//                String[] classUrls = (String[]) objectProperties.getValueOrDefault("url", Collections.emptyList());
//                String[] methodUrls = (String[]) methodProperties.getValueOrDefault("url", Collections.emptyList());
//                Object object = annotatedObjectProperties.getObject();
//                Method javaMethod = annotatedMethodProperties.getJavaMethod();
//                return createEndPointMethodInfos(httpMethods, classUrls, methodUrls, object, javaMethod);
//            })
//            .flatMap(Collection::stream)
//            .collect(Collectors.toUnmodifiableList());
//
//        // 5. endPointTask create.
//        PathMatcher<InstanceMethod> pathMatcher =
//            endPointJavaMethodInfos.stream().reduce(PathMatcher.empty(), (pm, em) -> {
//                String requestMethodName = em.getRequestMethod().name();
//                Token token = new Token(requestMethodName);
//
//                String url = em.getUrl();
//                PathUrl pathUrl = PathUrl.of(url);
//
//                Method javaMethod = em.getJavaMethod();
//                Object object = objectRepository.findObjectByMethod(javaMethod)
//                    .orElseThrow(() -> new RuntimeException("does not exist object."));
//                InstanceMethod instanceMethod = new InstanceMethod(object, javaMethod);
//
//                return pm.add(token, pathUrl, instanceMethod);
//            }, PathMatcher::concat);
////        ResourcePathFinder resourceFinder = ResourcePathFinder.from(App.class, "../../resources/main")
//
//        // 8. execute service.
//        SocketHttpTaskExecutor socketHttpTaskExecutor = SocketHttpTaskExecutor.create(INSTANCE.getPort(),
//                                                                                      INSTANCE.getMaxConnection(),
//                                                                                      INSTANCE.getWaitConnection(),
//                                                                                      INSTANCE.getKeepAliveTime());
//        log.info("server start.");
//        socketHttpTaskExecutor.execute(((request, response) -> {
//
//            Token token = new Token(RequestMethod.find(request.getHttpMethod().name()).name());
//            PathUrl requestUrl = PathUrl.of(request.getHttpRequestPath().getValue().toString());
//            MatchedElement<InstanceMethod> matchedElement = pathMatcher.match(token, requestUrl).orElseThrow(() -> new RuntimeException("does not exist matched element"));
//
//            InstanceMethod element1 = matchedElement.getElement();
//            matcher.path.PathVariable pathVariable = matchedElement.getPathVariable();
////            Method element = matchedElement.getElement();
//
////            EndPointTaskWorker2 endPointTaskWorker = matchedEndPointTaskWorker.getEndPointTaskWorker();
////            UrlParameterValues pathVariableValue = new UrlParameterValues(matchedEndPointTaskWorker.getPathVariableValue().getValues());
////            UrlParameterValues queryParamValues = new UrlParameterValues(request.getQueryParameters().getParameterMap());
////            InputStream bodyInputStream = request.getBodyInputStream();
////
////            ParameterValueAssignees2 parameterValueAssignees2 = new ParameterValueAssignees2(
////                Map.of(URL, new HttpUrlParameterValueAssignee(pathVariableHttpUrlParameterInfoFunction(annotationPropertyGetter), pathVariableValue),
////                       QUERY_PARAM, new HttpUrlParameterValueAssignee(requestParamHttpUrlParameterInfoFunction(annotationPropertyGetter), queryParamValues),
////                       BODY, new HttpBodyParameterValueAssignee(requestBodyHttpUrlParameterInfoFunction(annotationPropertyGetter), bodyInputStream)));
////            EndPointTaskExecutor endPointTaskExecutor = new EndPointTaskExecutor(parameterValueAssignees2);
////            EndPointWorkerResult endPointWorkerResult = endPointTaskExecutor.execute(endPointTaskWorker);
////
////            WorkerResultType type = endPointWorkerResult.getType();
////            Object result = endPointWorkerResult.getResult();
////            ContentType2 contentType = getContentType2(type);
////            InputStream content = VALUE_TYPE_CONVERTER.convertToInputStream(result);
////            HttpResponseHeaderCreator2 headerCreator = new HttpResponseHeaderCreator2(SIMPLE_DATE_FORMAT, HOST_ADDRESS, contentType);
////            HttpResponseHeader httpResponseHeader = headerCreator.create();
////            HttpResponseSender httpResponseSender = new HttpResponseSender(response);
////            httpResponseSender.send(httpResponseHeader, content);
//        }));
    }

    private static ContentType2 getContentType2(WorkerResultType type) {
        if (type == EMPTY) {
            return null;
        }
        return convertToContentType(type);
    }

    private static List<PreTaskInfo> createPreTaskInfos(PreTaskWorker preTaskWorker, String filterName, String[] patterns) {
        return Arrays.stream(patterns)
            .map(pattern -> new PreTaskInfo(filterName, pattern, preTaskWorker))
            .collect(Collectors.toUnmodifiableList());
    }

    private static List<PostTaskInfo> createPostTaskInfos(PostTaskWorker postTaskWorker, String filterName, String[] patterns) {
        return Arrays.stream(patterns)
            .map(pattern -> new PostTaskInfo(filterName, pattern, postTaskWorker))
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


