package dto;

import java.io.InputStream;
import static validate.ValidateUtil.validateNull;

public class ResponseData {
    private final Status status;
    private final ContentType contentType;
    private final InputStream inputStream;

    public ResponseData(Status status, ContentType contentType, InputStream inputStream) {
        this.status = validateNull(status);
        this.contentType = validateNull(contentType);
        this.inputStream = validateNull(inputStream);
    }

    public Status getStatus() {
        return status;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
