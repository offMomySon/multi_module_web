package com.main.executor;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import mapper.HttpPathMatcherIf;
import marker.PathVariable;
import marker.RequestBody;
import marker.RequestMethod;
import marker.RequestParam;
import processor.HttpRequestExecutor;
import variableExtractor.CompositeParameterConverter;
import variableExtractor.ParameterConverter;
import variableExtractor.RequestBodyParameterConverter;
import variableExtractor.RequestParameterConverter;
import vo.BodyContent;
import vo.HttpRequest;
import vo.RequestResult;
import vo.RequestValues;
import static java.nio.charset.StandardCharsets.UTF_8;
import static mapper.HttpPathMatcher.MatchedMethod;

@Slf4j
public class RequestExecutor implements HttpRequestExecutor {
    private final MethodExecutor methodExecutor;
    private final HttpPathMatcherIf httpPathMatcher;

    public RequestExecutor(MethodExecutor methodExecutor, HttpPathMatcherIf httpPathMatcher) {
        Objects.requireNonNull(methodExecutor, "methodExecutor require not null.");
        Objects.requireNonNull(httpPathMatcher, "httpPathMatcher require not null.");

        this.methodExecutor = methodExecutor;
        this.httpPathMatcher = httpPathMatcher;
    }

    @Override
    public RequestResult execute(HttpRequest httpRequest) {
        try {
            RequestMethod method = RequestMethod.find(httpRequest.getHttpMethod().name());
            String url = httpRequest.getHttpUri().getUrl();
            RequestValues formVariable = new RequestValues(httpRequest.getQueryParameters().getParameterMap());
            BodyContent bodyContent = BodyContent.from(httpRequest.getRequestStream());

            String result = doExecute(method, url, formVariable, bodyContent);

            String startLine = "HTTP/1.1 200 OK";
            Map<String, String> header = Map.of("Host", "localhost:8080");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(result.getBytes(UTF_8));

            return new RequestResult(startLine, header, byteArrayInputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String doExecute(RequestMethod method, String url, RequestValues formVariable, BodyContent bodyContent) {
        MatchedMethod matchedMethod = httpPathMatcher.matchJavaMethod(method, url).orElseThrow(() -> new RuntimeException(""));

        Method javaMethod = matchedMethod.getJavaMethod();
        RequestValues pathVariable = matchedMethod.getPathVariable();

        Map<Class<? extends Annotation>, ParameterConverter> classParameterConverterMap = Map.of(RequestParam.class, new RequestParameterConverter(RequestParam.class, formVariable),
                                                                                                 PathVariable.class, new RequestParameterConverter(PathVariable.class, pathVariable),
                                                                                                 RequestBody.class, new RequestBodyParameterConverter(bodyContent));
        CompositeParameterConverter compositeParameterConverter = new CompositeParameterConverter(classParameterConverterMap);

        Optional<Object> result = methodExecutor.execute(javaMethod, compositeParameterConverter);

        if (result.isPresent()) {
            return "success to execute";
        }
        return "failt to execute";
    }
}
