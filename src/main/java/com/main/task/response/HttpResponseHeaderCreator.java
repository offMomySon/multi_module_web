package com.main.task.response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public abstract class HttpResponseHeaderCreator {
    protected final SimpleDateFormat simpleDateFormat;
    protected final String hostAddress;

    public HttpResponseHeaderCreator(SimpleDateFormat simpleDateFormat, String hostAddress) {
        Objects.requireNonNull(simpleDateFormat);
        Objects.requireNonNull(hostAddress);

        if (hostAddress.isBlank()) {
            throw new RuntimeException("hostAddress is empty.");
        }

        this.simpleDateFormat = simpleDateFormat;
        this.hostAddress = hostAddress;
    }

    public HttpResponseHeader create() {
        String startLine = "HTTP/1.1 200 OK";
        Map<String, String> header = Map.of(
            "Date", simpleDateFormat.format(new Date()),
            "Host", hostAddress,
            "Content-Type", extractContentType(),
            "Connection", "close"
        );

        return new HttpResponseHeader(startLine, header);
    }

    public abstract String extractContentType();
}