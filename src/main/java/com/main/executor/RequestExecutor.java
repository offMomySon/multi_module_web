package com.main.executor;

import annotation.PathVariable;
import annotation.RequestBody;
import annotation.RequestMethod;
import annotation.RequestParam;
import converter.Converter;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import mapper.HttpPathMatcher;
import mapper.segment.PathUrl;
import mapper.segment.PathVariableValue;
import processor.HttpRequestExecutor;
import variableExtractor.CompositeParameterConverter;
import variableExtractor.ParameterConverter;
import variableExtractor.RequestBodyParameterConverter;
import variableExtractor.RequestParameterConverter;
import vo.BodyContent;
import vo.HttpRequest;
import vo.RequestResult;
import vo.RequestValues;
import static mapper.BaseHttpPathMatcher.MatchedMethod;

@Slf4j
public class RequestExecutor implements HttpRequestExecutor {
    private final MethodExecutor methodExecutor;
    private final HttpPathMatcher httpPathMatcher;
    private final Converter converter;

    public RequestExecutor(MethodExecutor methodExecutor, HttpPathMatcher httpPathMatcher, Converter converter) {
        Objects.requireNonNull(methodExecutor, "methodExecutor require not null.");
        Objects.requireNonNull(httpPathMatcher, "httpPathMatcher require not null.");

        this.converter = converter;
        this.methodExecutor = methodExecutor;
        this.httpPathMatcher = httpPathMatcher;
    }

    @Override
    public RequestResult execute(HttpRequest httpRequest) {
        Objects.requireNonNull(httpRequest);

        try {
            RequestMethod method = RequestMethod.find(httpRequest.getHttpMethod().name());
            String requestUrl = httpRequest.getHttpUri().getUrl();
            RequestValues formVariable = new RequestValues(httpRequest.getQueryParameters().getParameterMap());
            BodyContent bodyContent = BodyContent.from(httpRequest.getRequestStream());

            Object o = doExecute(method, requestUrl, formVariable, bodyContent);

            InputStream inputStream = converter.convertToInputStream(o);

            String startLine = "HTTP/1.1 200 OK";
            Map<String, String> header = Map.of(
                "Date", "MON, 27 Jul 2023 12:28:53 GMT",
                "Host", "localhost:8080",
                "Content-Type", "text/html; charset=UTF-8"
//                "Content-Length", result.length() + ""
            );
//            InputStream inputStream = new ByteArrayInputStream(result.getBytes(UTF_8));

            return new RequestResult(startLine, header, inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object doExecute(RequestMethod method, String requestUrl, RequestValues formVariable, BodyContent bodyContent) {
        PathUrl requestPathUrl = PathUrl.from(requestUrl);
        MatchedMethod matchedMethod = httpPathMatcher.matchJavaMethod(method, requestPathUrl).orElseThrow(() -> new RuntimeException(""));

        Method javaMethod = matchedMethod.getJavaMethod();
        PathVariableValue pathVariableValue = matchedMethod.getPathVariableValue();

        Map<Class<? extends Annotation>, ParameterConverter> classParameterConverterMap = Map.of(RequestParam.class, new RequestParameterConverter(RequestParam.class, formVariable),
                                                                                                 PathVariable.class, RequestParameterConverter.from(PathVariable.class, pathVariableValue),
                                                                                                 RequestBody.class, new RequestBodyParameterConverter(bodyContent));
        CompositeParameterConverter compositeParameterConverter = new CompositeParameterConverter(classParameterConverterMap);

        Optional<Object> result = methodExecutor.execute(javaMethod, compositeParameterConverter);

        if (result.isEmpty()) {
            return "emtpy";
        }

        return result.get();
    }
}
