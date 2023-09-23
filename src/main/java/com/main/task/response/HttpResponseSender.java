package com.main.task.response;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import converter.CompositeConverter;
import response.HttpResponseHeader;
import vo.HttpResponse;
import vo.HttpResponseWriter;

public class HttpResponseSender {
    private static final CompositeConverter converter = new CompositeConverter();

    private final HttpResponse httpResponse;

    public HttpResponseSender(HttpResponse httpResponse) {
        Objects.requireNonNull(httpResponse);
        this.httpResponse = httpResponse;
    }

    public void send(HttpResponseHeader responseHeader, InputStream methodResult) {
        String startLine = responseHeader.getStartLine();
        Map<String, String> header = responseHeader.getHeader();

        InputStream inputStream = converter.convertToInputStream(methodResult);

        httpResponse.setStartLine(startLine);
        httpResponse.appendHeader(header);
        HttpResponseWriter sender = httpResponse.getSender();
        sender.send(inputStream);
    }
}