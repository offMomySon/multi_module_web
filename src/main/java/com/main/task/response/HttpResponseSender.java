package com.main.task.response;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import converter.CompositeConverter;
import vo.HttpResponse;
import vo.HttpResponseWriter;

public class HttpResponseSender {
    private static final CompositeConverter converter = new CompositeConverter();

    private final HttpResponse httpResponse;

    public HttpResponseSender(HttpResponse httpResponse) {
        Objects.requireNonNull(httpResponse);
        this.httpResponse = httpResponse;
    }

    public void send(HttpResponseHeader responseHeader, Optional<Object> optionalResult) {
        String startLine = responseHeader.getStartLine();
        Map<String, String> header = responseHeader.getHeader();

        Object methodResult = optionalResult.isEmpty() ? null : optionalResult.get();
        InputStream inputStream = converter.convertToInputStream(methodResult);

        httpResponse.setStartLine(startLine);
        httpResponse.appendHeader(header);
        HttpResponseWriter sender = httpResponse.getSender();
        sender.send(inputStream);
    }
}