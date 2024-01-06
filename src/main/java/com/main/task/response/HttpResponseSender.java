package com.main.task.response;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import com.main.util.converter.CompositeValueTypeConverter;
import response.HttpResponseHeader;
import vo.HttpResponse;
import vo.HttpResponseWriter;

public class HttpResponseSender {
    private static final CompositeValueTypeConverter converter = new CompositeValueTypeConverter();

    private final HttpResponse httpResponse;

    public HttpResponseSender(HttpResponse httpResponse) {
        Objects.requireNonNull(httpResponse);
        this.httpResponse = httpResponse;
    }

    public void send(HttpResponseHeader responseHeader, InputStream inputStream) {
        String startLine = responseHeader.getStartLine();
        Map<String, String> header = responseHeader.getHeader();

        httpResponse.setStartLine(startLine);
        httpResponse.appendHeader(header);
        HttpResponseWriter sender = httpResponse.getSender();
        sender.send(inputStream);
    }
}