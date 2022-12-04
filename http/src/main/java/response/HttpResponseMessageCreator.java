package response;

import dto.ContentType;
import dto.Status;
import io.IoUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.validateNull;

public class HttpResponseMessageCreator {
    //    Date: Thu, 16 Jan 2016 08:16:18 GMT
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
    private static final String CONTENT_LENGTH_MESSAGE = "Content-Length : ";
    private static final String END_OF_LINE = "\r\n";

    private final Status status;
    private final ContentType contentType;
    private final Integer contentLength;
    private final BufferedInputStream contentStream;

    private byte[] BUFFER = new byte[8192];

    public HttpResponseMessageCreator(Status status, ContentType contentType, Integer contentLength, InputStream contentStream) {
        this.status = validateNull(status);
        this.contentType = validateNull(contentType);
        this.contentLength = validateNull(contentLength);
        this.contentStream = validateNull(IoUtils.createBufferedInputStream(contentStream));
    }

    public byte[] generateHeader() {
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(status.getStatusLine()).append(END_OF_LINE);
        headerBuilder.append(DATE_FORMAT.format(new Date())).append(END_OF_LINE);
        headerBuilder.append(CONTENT_LENGTH_MESSAGE).append(contentLength).append(END_OF_LINE);
        headerBuilder.append(contentType.getHeaderContent()).append(END_OF_LINE);

        return headerBuilder.toString().getBytes(UTF_8);
    }

    public byte[] generateSeparator() {
        return END_OF_LINE.getBytes(UTF_8);
    }

    public byte[] generateContent() {
        try {
            contentStream.read(BUFFER);
            return BUFFER;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLeftContent() {
        try {
            return contentStream.available() != 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}