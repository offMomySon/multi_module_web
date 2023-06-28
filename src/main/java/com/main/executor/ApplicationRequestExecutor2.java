package com.main.executor;

import com.main.App;
import lombok.extern.slf4j.Slf4j;
import matcher.RequestMethod;
import matcher.converter.*;
import matcher.converter.base.CompositeConverter;
import matcher.segment.PathUrl;
import processor.HttpRequestExecutor;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.QueryParameters;

import java.util.Objects;

@Slf4j
public class ApplicationRequestExecutor2 implements HttpRequestExecutor {
    private static final CompositeConverter converter = new CompositeConverter();
    private final App.BaseRequestExecutor baseRequestExecutor;

    public ApplicationRequestExecutor2(App.BaseRequestExecutor baseRequestExecutor) {
        this.baseRequestExecutor = baseRequestExecutor;
    }

    @Override
    public boolean execute(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        RequestMethod method = RequestMethod.find(request.getHttpMethod().name());
        PathUrl requestUrl = PathUrl.from(request.getHttpUri().getUrl());
        QueryParameters queryParameters = request.getQueryParameters();
        BodyContent bodyContent = BodyContent.from(request.getBodyInputStream());

//        Object o = requestExecutor.execute(method, requestUrl, queryParameters, bodyContent);
//
//        InputStream inputStream = converter.convertToInputStream(o);
//
//        response.setStartLine("HTTP/1.1 200 OK");
//        response.appendHeader(Map.of(
//            "Date", "MON, 27 Jul 2023 12:28:53 GMT",
//            "Host", "localhost:8080",
//            "Content-Type", "text/html; charset=UTF-8"));
//        HttpResponseWriter sender = response.getSender();
//        sender.send(inputStream);

        return true;
    }
}
