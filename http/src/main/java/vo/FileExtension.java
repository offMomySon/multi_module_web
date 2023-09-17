package vo;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import static vo.ContentType.APPLICATION_JSON;
import static vo.ContentType.IMAGE_GIF;
import static vo.ContentType.IMAGE_JPEG;
import static vo.ContentType.TEXT_HTML;

public enum FileExtension {
    JPG(IMAGE_JPEG),
    GIF(IMAGE_GIF),
    TXT(TEXT_HTML),
    JSON(APPLICATION_JSON);

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
