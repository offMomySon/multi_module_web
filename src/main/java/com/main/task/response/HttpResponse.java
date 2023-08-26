package com.main.task.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpResponse {
    private final String startLine;
    private final Map<String, String> header;

    public HttpResponse(String startLine, Map<String, String> header) {
        Objects.requireNonNull(startLine);
        Objects.requireNonNull(header);
        if (startLine.isBlank()) {
            throw new RuntimeException("startLine is empty.");
        }

        header = header.entrySet().stream()
            .filter(e -> Objects.nonNull(e.getKey()))
            .filter(e -> Objects.nonNull(e.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        if (header.isEmpty()) {
            throw new RuntimeException("header is empty.");
        }

        this.startLine = startLine;
        this.header = header;
    }

    public String getStartLine() {
        return startLine;
    }

    public Map<String, String> getHeader() {
        return new HashMap<>(header);
    }
}
