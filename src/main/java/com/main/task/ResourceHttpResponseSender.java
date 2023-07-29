package com.main.task;

import com.main.task.ResourceHttpResponseHeaderValueCreator.HttpHeaderValue;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import vo.HttpResponse;
import vo.HttpResponseWriter;

public class ResourceHttpResponseSender {
    private final HttpResponse httpResponse;
    private final HttpHeaderValue httpHeaderValue;
    private final Path resource;

    public ResourceHttpResponseSender(HttpResponse httpResponse, HttpHeaderValue httpHeaderValue, Path resource) {
        this.httpResponse = httpResponse;
        this.httpHeaderValue = httpHeaderValue;
        this.resource = resource;
    }

    public void send() {
        String startLine = httpHeaderValue.getStartLine();
        Map<String, String> headerValue = httpHeaderValue.getHeaderValue();

        httpResponse.setStartLine(startLine);
        httpResponse.appendHeader(headerValue);

        InputStream fileInputStream = getResourceInputStream(resource);
        HttpResponseWriter sender = httpResponse.getSender();
        sender.send(fileInputStream);
    }

    private static InputStream getResourceInputStream(Path resource) {
        try {
            return new BufferedInputStream(new FileInputStream(resource.toString()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
