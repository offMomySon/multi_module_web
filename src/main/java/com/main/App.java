package com.main;


import com.main.extractor.ParameterValueExtractor;
import com.main.extractor.ParameterValueExtractorStrategy;
import com.main.filter.ApplicationWebFilterCreator;
import com.main.filter.WebFilterComponentFilterCreator;
import com.main.invoker.MethodInvoker;
import container.ClassFinder;
import container.ComponentContainerCreator;
import container.Container;
import container.annotation.Component;
import container.annotation.Controller;
import filter.Filters;
import filter.annotation.WebFilter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import matcher.BaseHttpPathMatcher.MatchedMethod;
import matcher.ControllerHttpPathMatcherCreator;
import matcher.HttpPathMatcher;
import matcher.RequestMethod;
import matcher.converter.BodyContent;
import matcher.converter.RequestParameters;
import matcher.converter.base.CompositeConverter;
import matcher.converter.base.ObjectConverter;
import matcher.segment.PathUrl;
import processor.HttpRequestExecutor;
import processor.HttpService;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.HttpResponseWriter;

@Slf4j
public class App {
    private static final Class<Component> COMPONENT_CLASS = Component.class;
    private static final Class<Controller> CONTROLLER_CLASS = Controller.class;
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;
    private static final Objects EMPTY_VALUE = null;

    private static final ObjectConverter objectConverter = new ObjectConverter();

    public static void main(String[] args) {
        // 1. class 를 모두 찾아옴.
        List<Class<?>> clazzes = ClassFinder.from(App.class, "com.main.business").findClazzes();

        // 2. class 로 container 를 생성.
        ComponentContainerCreator componentContainerCreator = new ComponentContainerCreator(clazzes);
        Container container = componentContainerCreator.create();

        // 3. class 로 httpPathMatcher 를 생성.
        ControllerHttpPathMatcherCreator controllerHttpPathMatcherCreator = new ControllerHttpPathMatcherCreator(clazzes);
        HttpPathMatcher httpPathMatcher = controllerHttpPathMatcherCreator.create();

        // 4. class 로 webfilter 를 생성.
        WebFilterComponentFilterCreator webFilterComponentFilterCreator = new WebFilterComponentFilterCreator(container);
        ApplicationWebFilterCreator applicationWebFilterCreator = new ApplicationWebFilterCreator(webFilterComponentFilterCreator, clazzes);
        Filters filters = applicationWebFilterCreator.create();

        // 5. executor 를 생성.
        BaseRequestExecutor baseRequestExecutor = new BaseRequestExecutor(container, httpPathMatcher);
        HttpService httpService = new HttpService(baseRequestExecutor, filters);
        httpService.start();
    }

    public static class BaseRequestExecutor implements HttpRequestExecutor {
        private static final CompositeConverter converter = new CompositeConverter();

        private final Container container;
        private final HttpPathMatcher httpPathMatcher;

        public BaseRequestExecutor(Container container, HttpPathMatcher httpPathMatcher) {
            this.container = container;
            this.httpPathMatcher = httpPathMatcher;
        }

        @Override
        public boolean execute(HttpRequest request, HttpResponse response) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(response);

            String methodName = request.getHttpMethod().name();
            String url = request.getHttpUri().getUrl();

            RequestMethod method = RequestMethod.find(methodName);
            PathUrl pathUrl = PathUrl.from(url);
            MatchedMethod matchedMethod = httpPathMatcher.matchJavaMethod(method, pathUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));

            RequestParameters pathVariableValue = new RequestParameters(matchedMethod.getPathVariableValue().getValues());
            RequestParameters queryParamValues = new RequestParameters(request.getQueryParameters().getParameterMap());
            BodyContent bodyContent = BodyContent.from(request.getBodyInputStream());
            ParameterValueExtractorStrategy parameterValueExtractorStrategy = new ParameterValueExtractorStrategy(pathVariableValue, queryParamValues, bodyContent);

            MethodInvoker methodInvoker = new MethodInvoker(container, parameterValueExtractorStrategy);
            Object result = methodInvoker.invoke(matchedMethod.getJavaMethod());

            // [시스템 컴포넌트적 요소 존재.]
            // 9. 응답값 생성.
            // as is.
            // (1) result -> to input stream.
            // (2) 임의의 header 셋팅
            // to be.
            // (1) 응닶 타입에 따라 content-type 설정.
            InputStream inputStream = converter.convertToInputStream(result);
            response.setStartLine("HTTP/1.1 200 OK");
            response.appendHeader(Map.of(
                "Date", "MON, 27 Jul 2023 12:28:53 GMT",
                "Host", "localhost:8080",
                "Content-Type", "text/html; charset=UTF-8"));
            HttpResponseWriter sender = response.getSender();
            sender.send(inputStream);

            return true;
        }

        private Object invoke(Method javaMethod, ParameterValueExtractorStrategy parameterValueExtractorStrategy) {
            Class<?> declaringClass = javaMethod.getDeclaringClass();
            Object instance = container.get(declaringClass);
            log.info("declaringClass : {}", declaringClass);
            log.info("instance : {}", instance);
            log.info("javaMethod : {}", javaMethod);

            // -> todo, parameter 에서 값 변환은 사전에 진행되었어야한다.
            // 8.1.
            // as is. (1) parameter -> value 변환.
            // to be. (1) nothing.
            Object[] values = Arrays.stream(javaMethod.getParameters())
                .map(parameterValueExtractorStrategy::create)
                .map(ParameterValueExtractor::extract)
                .map(extractValue -> {
                    String value = extractValue.getOptionalValue().orElse("");
                    Class<?> parameterType = extractValue.getParameterType();
                    return objectConverter.convert(value, parameterType);
                })
                .peek(value -> log.info("value : {}, {}", value, value.getClass()))
                .toArray();

            // 8.2. 실행.
            return doExecute(instance, javaMethod, values);
        }

        private static Object doExecute(Object object, Method javaMethod, Object[] paramsValues) {
            try {
                log.info("object : {}, javaMethod : {}, paramsValues : {}", object.getClass(), javaMethod, paramsValues);
                return javaMethod.invoke(object, paramsValues);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}