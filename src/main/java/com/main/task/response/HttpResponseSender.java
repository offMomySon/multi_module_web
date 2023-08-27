package com.main.task.response;

import java.io.InputStream;

public class HttpResponseSender {
    private final HttpResponseHeader httpResponseHeader;
    private final InputStream bodyStream;

    public HttpResponseSender(HttpResponseHeader httpResponseHeader, InputStream bodyStream) {
        this.httpResponseHeader = httpResponseHeader;
        this.bodyStream = bodyStream;
    }
}
