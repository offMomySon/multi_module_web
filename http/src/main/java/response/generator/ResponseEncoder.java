package response.generator;

import dto.ContentType;
import dto.Status;
import java.io.InputStream;
import static validate.ValidateUtil.*;

public abstract class ResponseEncoder {
    protected static final String END_OF_LINE = "\r\n";
    protected static final String DATE_HEADER = "Date : ";

    protected final Status status;
    protected final ContentType contentType;
    protected final InputStream contentInputStream;

    protected ResponseEncoder(Status status, ContentType contentType, InputStream contentInputStream) {
        this.status = validateNull(status);
        this.contentType = validateNull(contentType);
        this.contentInputStream = validateNull(contentInputStream);
    }

    public abstract byte[] encode();
}
