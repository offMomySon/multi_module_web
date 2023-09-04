package com.main.task.response;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import static com.main.task.response.ContentType.IMAGE_GIF;
import static com.main.task.response.ContentType.IMAGE_JPEG;
import static com.main.task.response.ContentType.TEXT_HTML;

public enum FileExtension {
    JPG(IMAGE_JPEG),
    GIF(IMAGE_GIF),
    TXT(TEXT_HTML);

    private final ContentType contentType;

    FileExtension(ContentType contentType) {
        Objects.requireNonNull(contentType);
        this.contentType = contentType;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public static FileExtension find(String fileExtension) {
        return Arrays.stream(FileExtension.values())
            .filter(value -> value.name().equalsIgnoreCase(fileExtension))
            .findAny()
            .orElseThrow(() -> new RuntimeException(MessageFormat.format("Does not exist match fileExtension. Find fileExtension : `{}`", fileExtension)));
    }
}
