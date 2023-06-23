package com.main.executor;

import com.main.matcher.HttpPathMatcher;
import com.main.matcher.RequestMethod;
import com.main.matcher.annotation.PathVariable;
import com.main.matcher.annotation.RequestBody;
import com.main.matcher.annotation.RequestParam;
import com.main.matcher.converter.BodyContent;
import com.main.matcher.converter.CompositeParameterConverter;
import com.main.matcher.converter.ParameterConverter;
import com.main.matcher.converter.RequestBodyParameterConverter;
import com.main.matcher.converter.RequestParameterConverter;
import com.main.matcher.converter.RequestParameters;
import com.main.matcher.converter.base.Converter;
import com.main.matcher.segment.PathUrl;
import com.main.matcher.segment.PathVariableValue;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import processor.HttpRequestExecutor;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.HttpResponseWriter;
import vo.QueryParameters;
import static com.main.matcher.BaseHttpPathMatcher.MatchedMethod;

@Slf4j
public class ApplicationRequestExecutor implements HttpRequestExecutor {
    private final MethodExecutor methodExecutor;
    private final HttpPathMatcher httpPathMatcher;
    private final Converter converter;

    public ApplicationRequestExecutor(MethodExecutor methodExecutor, HttpPathMatcher httpPathMatcher, Converter converter) {
        Objects.requireNonNull(methodExecutor, "methodExecutor require not null.");
        Objects.requireNonNull(httpPathMatcher, "httpPathMatcher require not null.");

        this.methodExecutor = methodExecutor;
        this.httpPathMatcher = httpPathMatcher;
        this.converter = converter;
    }

    @Override
    public boolean execute(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        RequestMethod method = RequestMethod.find(request.getHttpMethod().name());
        String requestUrl = request.getHttpUri().getUrl();
        QueryParameters queryParameters = request.getQueryParameters();
        BodyContent bodyContent = BodyContent.from(request.getBodyInputStream());

        Object o = doExecute(method, requestUrl, queryParameters, bodyContent);

        InputStream inputStream = converter.convertToInputStream(o);

        response.setStartLine("HTTP/1.1 200 OK");
        response.appendHeader(Map.of(
            "Date", "MON, 27 Jul 2023 12:28:53 GMT",
            "Host", "localhost:8080",
            "Content-Type", "text/html; charset=UTF-8"));
        HttpResponseWriter sender = response.getSender();
        sender.send(inputStream);

        return true;
    }

    private Object doExecute(RequestMethod method, String requestUrl, QueryParameters queryParameters, BodyContent bodyContent) {
        PathUrl requestPathUrl = PathUrl.from(requestUrl);
        MatchedMethod matchedMethod = httpPathMatcher.matchJavaMethod(method, requestPathUrl).orElseThrow(() -> new RuntimeException(""));

        Method javaMethod = matchedMethod.getJavaMethod();
        PathVariableValue pathVariableValue = matchedMethod.getPathVariableValue();
        RequestParameters queryParamValues = new RequestParameters(queryParameters.getParameterMap());

        Map<Class<? extends Annotation>, ParameterConverter> classParameterConverterMap = Map.of(RequestParam.class, new RequestParameterConverter(RequestParam.class, queryParamValues),
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
