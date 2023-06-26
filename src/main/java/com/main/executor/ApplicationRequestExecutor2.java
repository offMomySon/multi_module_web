package com.main.executor;

import com.main.App;
import lombok.extern.slf4j.Slf4j;
import matcher.RequestMethod;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestBody;
import matcher.annotation.RequestParam;
import matcher.converter.*;
import matcher.converter.base.CompositeConverter;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import processor.HttpRequestExecutor;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.HttpResponseWriter;
import vo.QueryParameters;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static matcher.BaseHttpPathMatcher.MatchedMethod;

@Slf4j
public class ApplicationRequestExecutor2 implements HttpRequestExecutor {
    private final App.RequestExecutor requestExecutor;
    private final CompositeConverter converter;

    public ApplicationRequestExecutor2(App.RequestExecutor requestExecutor, CompositeConverter converter) {
        this.requestExecutor = requestExecutor;
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

        Object o = requestExecutor.doExecute(method, requestUrl, queryParameters, bodyContent);

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
}
