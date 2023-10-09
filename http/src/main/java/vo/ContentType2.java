package vo;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

public enum ContentType2 {
    TEXT_PLAIN("text/plain"), // c
    TEXT_HTML("text/html"), // c
    TEXT_CSS("text/css"), // c

    IMAGE_JPEG("image/jpeg"),  // c
    IMAGE_GIF("image/gif"), // c
    IMAGE_PNG("image/png"), // c

    APPLICATION_JSON("application/json"), // c
    APPLICATION_JAVASCRIPT("application/javascript"), // c
    APPLICATION_JAVA_VM("application/java-vm");

    private final String value;

    ContentType2(String value) {
        Objects.requireNonNull(value);
        if (value.isBlank()) {
            throw new RuntimeException("value is empty.");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContentType2 find(String contentType) {
        return Arrays.stream(ContentType2.values())
            .filter(value -> value.name().equalsIgnoreCase(contentType))
            .findAny()
            .orElseThrow(() -> new RuntimeException(MessageFormat.format("Does not exist match contentType. Find contentType : `{}`", contentType)));
    }
}
