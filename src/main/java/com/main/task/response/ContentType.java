package com.main.task.response;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

public enum ContentType {
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),
    IMAGE_PLAIN("text/plain"),
    APPLICATION_JSON("application/json"),
    TEXT_HTML("text/html");

    private final String value;

    ContentType(String value) {
        Objects.requireNonNull(value);
        if (value.isBlank()) {
            throw new RuntimeException("value is empty.");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContentType find(String contentType) {
        return Arrays.stream(ContentType.values())
            .filter(value -> value.name().equalsIgnoreCase(contentType))
            .findAny()
            .orElseThrow(() -> new RuntimeException(MessageFormat.format("Does not exist match contentType. Find contentType : `{}`", contentType)));
    }
}
