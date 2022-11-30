package response;

import dto.ContentType;
import dto.Status;
import io.IoUtils;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.validateNull;

public class HttpHeaderSender {
    //    https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages
    //    https://gmlwjd9405.github.io/2019/01/28/http-header-types.html
    //    header 의 구성. general header, response header, entity header

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z"); // //    Date: Thu, 16 Jan 2016 08:16:18 GMT
    private static final String CONTENT_LENGTH = "Content-Length : ";
    private static final String END_OF_LINE = "\r\n";

    private final Status status;
    private final ContentType contentType;
    private final Integer contentLength;
    private final BufferedOutputStream bufferedOutputStream;

    public HttpHeaderSender(Status status, ContentType contentType, Integer contentLength, OutputStream outputStream) {
        this.status = validateNull(status);
        this.contentType = validateNull(contentType);
        this.contentLength = validateNull(contentLength);
        this.bufferedOutputStream = IoUtils.createBufferedOutputStream(validateNull(outputStream));
    }

    public void send() {
        byte[] headerBytes = generateHeader();

        try {
            bufferedOutputStream.write(headerBytes);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] generateHeader() {
        StringBuilder headerBuilder = new StringBuilder();

        headerBuilder = generateStatusLine(headerBuilder);
        headerBuilder = generateGeneralHeader(headerBuilder);
        headerBuilder = generateEntityHeader(headerBuilder);

        return headerBuilder.toString().getBytes(UTF_8);
    }

    private StringBuilder generateStatusLine(StringBuilder headerBuilder) {
        headerBuilder.append(status.getStatusLine()).append(END_OF_LINE);
        return headerBuilder;
    }

    private StringBuilder generateGeneralHeader(StringBuilder headerBuilder) {
        headerBuilder.append(DATE_FORMAT.format(new Date())).append(END_OF_LINE);
        return headerBuilder;
    }

    private StringBuilder generateEntityHeader(StringBuilder headerBuilder) {
        headerBuilder.append(contentType.getHeaderContent()).append(END_OF_LINE);
        headerBuilder.append(CONTENT_LENGTH).append(contentLength).append(END_OF_LINE);
        return headerBuilder;
    }


}

