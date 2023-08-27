package com.main.task.response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpResponseHeaderCreator {
    private final SimpleDateFormat simpleDateFormat;
    private final String hostAddress;
    private final Optional<String> contentType;

    public HttpResponseHeaderCreator(SimpleDateFormat simpleDateFormat, String hostAddress, String contentType) {
        Objects.requireNonNull(simpleDateFormat);
        Objects.requireNonNull(hostAddress);
        if (hostAddress.isBlank()) {
            throw new RuntimeException("hostAddress is empty.");
        }

        this.simpleDateFormat = simpleDateFormat;
        this.hostAddress = hostAddress;
        this.contentType = Optional.ofNullable(contentType);
    }

    public HttpResponseHeader create() {
        String startLine = "HTTP/1.1 200 OK";

        Map<String, String> header = new HashMap<>();
        header.put("Date", simpleDateFormat.format(new Date()));
        header.put("Host", hostAddress);
        contentType.ifPresent(s -> header.put("Content-Type", s));
        header.put("Connection", "close");

        return new HttpResponseHeader(startLine, header);
    }
}