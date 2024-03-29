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
import com.main.task.executor.BaseHttpRequestProcessor;
import com.main.util.AnnotationUtils;
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
import matcher.CompositedEndpointTaskMatcher;
import matcher.EndpointTaskMatcher;
import matcher.RequestMethod;
import matcher.StaticResourceEndPointTaskMatcher;
import matcher.creator.JavaMethodPathMatcherCreator;
import matcher.creator.EndPointMethodInfo;
import matcher.creator.ResourcePathFinder;
import parameter.extractor.FunctionBodyParameterInfoExtractor;
import parameter.extractor.FunctionHttpUrlParameterInfoExtractor;
import parameter.extractor.HttpBodyParameterInfoExtractor;
import parameter.extractor.HttpBodyParameterInfoExtractor.HttpBodyParameterInfo;
import parameter.extractor.HttpUrlParameterInfoExtractor;
import parameter.matcher.ParameterAndValueAssigneeType;
import pretask.PreTaskCreator;
import pretask.PreTaskInfo;
import task.PreTaskWorker;
import task.PreTasks;
import task.PreTasks.ReadOnlyPreTasks;
import static parameter.extractor.HttpUrlParameterInfoExtractor.HttpUrlParameterInfo;
import static parameter.matcher.ParameterValueAssigneeType.BODY;
import static parameter.matcher.ParameterValueAssigneeType.INPUT_STREAM;
import static parameter.matcher.ParameterValueAssigneeType.OUTPUT_STREAM;
import static parameter.matcher.ParameterValueAssigneeType.QUERY_PARAM;
import static parameter.matcher.ParameterValueAssigneeType.URL;

@Slf4j
public class App {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final String HOST_ADDRESS;
    private static final AnnotationPropertyMappers ANNOTATION_PROPERTY_MAPPERS;

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
        AnnotatedClassObjectRepository objectRepository = objectRepositoryCreator.fromPackage(App.class, "com.main");

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

        List<EndPointMethodInfo> endPointMethodInfos = requestMappedProperties.stream()
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
                return createRequestMappedMethods(httpMethods, classUrls, methodUrls, object, javaMethod);
            })
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());

        JavaMethodPathMatcherCreator javaMethodPathMatcherCreator = new JavaMethodPathMatcherCreator(customParameterParameterTypeInfoFunction());
        List<EndpointTaskMatcher> javaMethodEndpointTaskMatchers = endPointMethodInfos.stream()
            .map(javaMethodPathMatcherCreator::create)
            .collect(Collectors.toUnmodifiableList());

        // 4. resource http endpoint task 생성.
        ResourcePathFinder resourcePathFinder = ResourcePathFinder.from(App.class, "../../resources/main", "static");
        List<StaticResourceEndPointTaskMatcher> staticResourceEndPointTaskMatchers = resourcePathFinder.create();

        List<EndpointTaskMatcher> endpointTaskMatchers = Stream.concat(javaMethodEndpointTaskMatchers.stream(), staticResourceEndPointTaskMatchers.stream()).collect(Collectors.toUnmodifiableList());
        EndpointTaskMatcher endpointTaskMatcher = new CompositedEndpointTaskMatcher(endpointTaskMatchers);

        // 5. parameter info 해석기 생성.
        HttpBodyParameterInfoExtractor httpBodyParameterInfoExtractor = new FunctionBodyParameterInfoExtractor(requestBodyHttpUrlParameterInfoFunction(annotationPropertyGetter));
        HttpUrlParameterInfoExtractor requestParamHttpUrlParameterInfoExtractor = new FunctionHttpUrlParameterInfoExtractor(requestParameterHttpUrlParameterInfoFunction(annotationPropertyGetter));
        HttpUrlParameterInfoExtractor pathVariableHttpUrlParameterInfoExtractor = new FunctionHttpUrlParameterInfoExtractor(pathVariableHttpUrlParameterInfoFunction(annotationPropertyGetter));
//        ParameterTypeFinder parameterTypeFinder = new FunctionParameterTypeFinder(customParameterParameterTypeInfoFunction());

        // 6. http service start.
        BaseHttpRequestProcessor baseHttpRequestProcessor = new BaseHttpRequestProcessor(httpBodyParameterInfoExtractor,
                                                                                         requestParamHttpUrlParameterInfoExtractor,
                                                                                         pathVariableHttpUrlParameterInfoExtractor,
                                                                                         endpointTaskMatcher,
                                                                                         SIMPLE_DATE_FORMAT,
                                                                                         HOST_ADDRESS);

        // 7. execute service.
        SocketHttpTaskExecutor socketHttpTaskExecutor = SocketHttpTaskExecutor.create(HttpConfig.INSTANCE.getPort(),
                                                                                      HttpConfig.INSTANCE.getMaxConnection(),
                                                                                      HttpConfig.INSTANCE.getWaitConnection(),
                                                                                      HttpConfig.INSTANCE.getKeepAliveTime());
        log.info("server start.");
        socketHttpTaskExecutor.execute(((request, response) -> {
            List<PreTaskWorker> preTaskWorkers = preTasks.findFilterWorkers(request.getHttpRequestPath().getValue().toString());
            for (PreTaskWorker preTaskWorker : preTaskWorkers) {
                preTaskWorker.execute(request, response);
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

    private static List<EndPointMethodInfo> createRequestMappedMethods(RequestMethod[] requestMethods, String[] classUrls, String[] _methodUrls, Object object, Method javaMethod) {
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

    private static Function<Parameter, HttpUrlParameterInfo> requestParameterHttpUrlParameterInfoFunction(AnnotationPropertyGetter annotationPropertyGetter) {
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
    private static Function<Parameter, ParameterAndValueAssigneeType> customParameterParameterTypeInfoFunction() {
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
}


