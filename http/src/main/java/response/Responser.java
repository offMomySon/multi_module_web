package response;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import static java.nio.charset.StandardCharsets.UTF_8;
import static util.IoUtils.createBufferedOutputStream;
import static util.ValidateUtil.validateNull;

public class Responser implements Closeable {
    private static final String END_OF_LINE = "\r\n";
    private static final int BUFFER_SIZE = 8192;
    private static final String CONTENT_LENGTH_HEADER = "Content-Length : ";
    private static final String DATE_HEADER = "Date : ";

    private final byte[] BUFFER = new byte[BUFFER_SIZE];
    private final BufferedOutputStream socketOutputStream;
    private final InputStream contentInputStream;
    private final ContentType contentType;
    private final Status status;

    public Responser(BufferedOutputStream socketOutputStream, InputStream contentInputStream, ContentType contentType, Status status) {
        validateNull(socketOutputStream);
        validateNull(contentInputStream);
        validateNull(contentType);
        validateNull(status);

        this.socketOutputStream = socketOutputStream;
        this.contentInputStream = contentInputStream;
        this.contentType = contentType;
        this.status = status;
    }

    public static Responser create(OutputStream socketOutputStream, InputStream contentInputStream, ContentType contentType, Status status) {
        validateNull(socketOutputStream);
        validateNull(contentInputStream);
        validateNull(contentType);
        validateNull(status);

        return new Responser(createBufferedOutputStream(socketOutputStream), contentInputStream, contentType, status);
    }


//    text 타입과 jpg 타입이 다를것 같은데..
//    text 타입의 경우 string 을 전달해줘도 되자만, jpg 의 경우 string 이 아닌 바이트로 던져줘야할것 같은데.
    public void send() throws IOException {
        while (doesNotEndOfStream(contentInputStream)) {
            int readLength = contentInputStream.read(BUFFER, 0, BUFFER_SIZE);
            String content = new String(BUFFER, 0, readLength, UTF_8);

            String response = createResponse(content);

            doSend(response);
        }
    }

    private void doSend(String response) throws IOException {
        socketOutputStream.write(response.getBytes(UTF_8));
        socketOutputStream.flush();
    }

    private String createResponse(String content) {
        StringBuilder responseBuilder = new StringBuilder();

        setHeader(responseBuilder, content);
        setBody(responseBuilder, content);

        return responseBuilder.toString();
    }

    private void setBody(StringBuilder responseBuilder, String content) {
        responseBuilder.append(content).append(END_OF_LINE);
    }

    // con
    private void setHeader(StringBuilder responseBuilder, String content) {
        responseBuilder.append(status.responseLine).append(END_OF_LINE);
        responseBuilder.append(CONTENT_LENGTH_HEADER).append(content.length()).append(END_OF_LINE);
        responseBuilder.append(contentType.headerContent).append(END_OF_LINE);
        responseBuilder.append(DATE_HEADER).append(new Date()).append(END_OF_LINE);
        responseBuilder.append(END_OF_LINE);
    }

    private boolean doesNotEndOfStream(InputStream inputStream) throws IOException {
        return inputStream.available() != 0;
    }


    public static Builder build() {
        return new Builder();
    }

    @Override
    public void close() throws IOException {
        socketOutputStream.close();

    }

    public static class Builder {
        private OutputStream socketOutputStream;
        private InputStream contentInputStream;
        private ContentType contentType;
        private Status status;

        private Builder() {
        }

        public Builder socketOutputStream(OutputStream outputStream) {
            this.socketOutputStream = outputStream;
            return this;
        }

        public Builder contentInputStream(InputStream inputStream) {
            this.contentInputStream = inputStream;
            return this;
        }

        public Builder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Responser build() {
            return Responser.create(socketOutputStream, contentInputStream, contentType, status);
        }

    }

    public enum ContentType {
        TEXT("Content-Type: text/html"),
        JPG("Content-Type: image/jpeg");

        private final String headerContent;

        ContentType(String headerContent) {
            this.headerContent = headerContent;
        }
    }

    public enum Status {
        FORBIDDEN("HTTP/1.1 403 Forbidden"),
        SERVICE_UNAVAILABLE("HTTP/1.1 503 Service Unavailable"),
        OK("HTTP/1.1 200 OK");

        private final String responseLine;

        Status(String responseLine) {
            this.responseLine = responseLine;
        }
    }
}
