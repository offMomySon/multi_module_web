package com.main.task.response;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

public enum FileExtension {
    JPG("image/jpeg"),
    GIF("image/gif"),
    TXT("text/plain");

    private final String contentType;

    FileExtension(String contentType) {
        Objects.requireNonNull(contentType);
        if (contentType.isBlank()) {
            throw new RuntimeException("contentType is empty.");
        }
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public static FileExtension find(String fileExtension) {
        return Arrays.stream(FileExtension.values())
            .filter(value -> value.name().equalsIgnoreCase(fileExtension))
            .findAny()
            .orElseThrow(() -> new RuntimeException(MessageFormat.format("Does not exist match contentType. Find contentType : `{}`", fileExtension)));
    }
}
