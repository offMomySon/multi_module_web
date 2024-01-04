package com.main;


import annotation.Component;
import annotation.Controller;
import annotation.Domain;
import annotation.PathVariable;
import annotation.PreWebFilter;
import annotation.Repository;
import annotation.RequestBody;
import annotation.RequestMapping;
import annotation.RequestParam;
import annotation.Service;
import com.main.finder.SystemResourceFinder2;
import com.main.util.AnnotationUtils;
import converter.CompositeValueTypeConverter;
import executor.SocketHttpTaskExecutor;
import instance.AnnotatedObjectRepository;
import instance.AnnotatedObjectRepository.AnnotatedObjectMethod;
import instance.AnnotatedObjectRepositoryCreator;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import matcher.PathMatcher;
import matcher.PathMatcher.MatchedElement;
import matcher.PathMatcher.Token;
import matcher.RequestMethod;
import matcher.path.PathUrl;
import parameter.UrlParameterValues;
import parameter.extractor.HttpBodyParameterInfoExtractor.HttpBodyParameterInfo;
import parameter.matcher.ParameterAndValueAssigneeType;
import task.worker.WorkerResultType;
import vo.ContentType2;
import static com.main.config.HttpConfig.INSTANCE;
import static matcher.RequestMethod.GET;
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
    private static final CompositeValueTypeConverter VALUE_TYPE_CONVERTER = new CompositeValueTypeConverter();

    static {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HOST_ADDRESS = getHostAddress();
    }

    public static void main(String[] args) {
//        // 1. annotating 된 class 의 instance 를 생성한다.
        AnnotatedObjectRepositoryCreator repositoryCreator = AnnotatedObjectRepositoryCreator
            .builder()
            .annotations(PreWebFilter.class,
                         Controller.class,
                         Component.class,
                         Domain.class,
                         Repository.class,
                         Service.class)
            .build();
        AnnotatedObjectRepository objectRepository = repositoryCreator.creaet(App4.class, "com.main");

        // 4. java http endpoint task 생성.
        List<AnnotatedObjectMethod> requestMappingAnnotatedMethods = objectRepository.findAnnotatedObjectMethodByClassAnnotatedClassAndMethodAnnotatedClass(Controller.class, RequestMapping.class);
        PathMatcher<InstanceMethod> controllerPathMatcher = requestMappingAnnotatedMethods.stream()
            .flatMap(requestMappingAnnotated -> {
                RequestMapping requestMapping = (RequestMapping) requestMappingAnnotated.getAnnotatedMethod().getAnnotation();

                List<Token> tokens = Arrays.stream(requestMapping.method())
                    .map(Enum::name)
                    .map(Token::new)
                    .collect(Collectors.toUnmodifiableList());
                List<PathUrl> pathUrls = Arrays.stream(requestMapping.url())
                    .map(PathUrl::of)
                    .collect(Collectors.toUnmodifiableList());

                Object object = requestMappingAnnotated.getAnnotatedObject().getObject();
                Method javaMethod = requestMappingAnnotated.getAnnotatedMethod().getMethod();
                InstanceMethod instanceMethod = new InstanceMethod(object, javaMethod);

                return tokens.stream()
                    .flatMap(token -> pathUrls.stream()
                        .map(pathUrl -> new TokenPathUrlInstanceMethod(token, pathUrl, instanceMethod)));
            })
            .reduce(PathMatcher.empty(), (pm, tokenPathUrlInstanceMethod) -> {
                Token token = tokenPathUrlInstanceMethod.getToken();
                PathUrl pathUrl = tokenPathUrlInstanceMethod.getPathUrl();
                InstanceMethod instanceMethod = tokenPathUrlInstanceMethod.getInstanceMethod();
                return pm.add(token, pathUrl, instanceMethod);
            }, PathMatcher::concat);

        SystemResourceFinder2 systemResourceFinder2 = SystemResourceFinder2.fromPackage(App.class, "../../resources/main", RESOURCE_PREFIX);
        InstanceMethod systemResourceInstanceMethod = new InstanceMethod(systemResourceFinder2, SystemResourceFinder2.getFindFileMethod());
        PathMatcher<InstanceMethod> pathMatcher = controllerPathMatcher.add(new Token(GET.name()),
                                                                            PathUrl.of(RESOURCE_PREFIX + "/**"),
                                                                            systemResourceInstanceMethod);

        // 8. execute service.
        SocketHttpTaskExecutor socketHttpTaskExecutor = SocketHttpTaskExecutor.create(INSTANCE.getPort(),
                                                                                      INSTANCE.getMaxConnection(),
                                                                                      INSTANCE.getWaitConnection(),
                                                                                      INSTANCE.getKeepAliveTime());
        log.info("server start.");
        socketHttpTaskExecutor.execute(((request, response) -> {
            Token token = new Token(RequestMethod.find(request.getHttpMethod().name()).name());
            PathUrl requestUrl = PathUrl.of(request.getHttpRequestPath().getValue().toString());
            MatchedElement<InstanceMethod> matchedElement = pathMatcher.match(token, requestUrl).orElseThrow(() -> new RuntimeException("does not exist matched element"));

            InstanceMethod instanceMethod = matchedElement.getElement();
            matcher.path.PathVariable pathVariable = matchedElement.getPathVariable();

//            EndPointTaskWorker2 endPointTaskWorker = matchedEndPointTaskWorker.getEndPointTaskWorker();
            UrlParameterValues pathVariableValue = new UrlParameterValues(pathVariable.getValues());
            UrlParameterValues queryParamValues = new UrlParameterValues(request.getQueryParameters().getParameterMap());
            InputStream bodyInputStream = request.getBodyInputStream();

//            ParameterValueAssignees2 parameterValueAssignees2 = new ParameterValueAssignees2(
//                Map.of(URL, new HttpUrlParameterValueAssignee(pathVariableHttpUrlParameterInfoFunction(annotationPropertyGetter), pathVariableValue),
//                       QUERY_PARAM, new HttpUrlParameterValueAssignee(requestParamHttpUrlParameterInfoFunction(annotationPropertyGetter), queryParamValues),
//                       BODY, new HttpBodyParameterValueAssignee(requestBodyHttpUrlParameterInfoFunction(annotationPropertyGetter), bodyInputStream)));
//            EndPointTaskExecutor endPointTaskExecutor = new EndPointTaskExecutor(parameterValueAssignees2);
//            EndPointWorkerResult endPointWorkerResult = endPointTaskExecutor.execute(endPointTaskWorker);
//
//            WorkerResultType type = endPointWorkerResult.getType();
//            Object result = endPointWorkerResult.getResult();
//            ContentType2 contentType = getContentType2(type);
//            InputStream content = VALUE_TYPE_CONVERTER.convertToInputStream(result);
//            HttpResponseHeaderCreator2 headerCreator = new HttpResponseHeaderCreator2(SIMPLE_DATE_FORMAT, HOST_ADDRESS, contentType);
//            HttpResponseHeader httpResponseHeader = headerCreator.create();
//            HttpResponseSender httpResponseSender = new HttpResponseSender(response);
//            httpResponseSender.send(httpResponseHeader, content);
        }));
    }

    private static ContentType2 getContentType2(WorkerResultType type) {
        if (type == EMPTY) {
            return null;
        }
        return convertToContentType(type);
    }

    private static String getHostAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static Function<Parameter, HttpUrlParameterInfo> requestParamHttpUrlParameterInfoFunction() {
        return parameter -> {
            RequestParam requestParam = AnnotationUtils.find(parameter, RequestParam.class).orElseThrow();

            String parameterName = requestParam.name();
            String defaultValue = requestParam.defaultValue();
            boolean required = requestParam.required();

            return new HttpUrlParameterInfo(parameterName, defaultValue, required);
        };
    }

    private static Function<Parameter, HttpUrlParameterInfo> pathVariableHttpUrlParameterInfoFunction() {
        return parameter -> {
            PathVariable pathVariable = AnnotationUtils.find(parameter, PathVariable.class).orElseThrow();

            String parameterName = pathVariable.name();
            boolean required = pathVariable.required();

            return new HttpUrlParameterInfo(parameterName, null, required);
        };
    }

    private static Function<Parameter, HttpBodyParameterInfo> requestBodyHttpUrlParameterInfoFunction() {
        return parameter -> {
            RequestBody requestBody = AnnotationUtils.find(parameter, RequestBody.class).orElseThrow();

            boolean required = requestBody.required();

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


