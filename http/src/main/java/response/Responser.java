package response;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import static io.IoUtils.createBufferedOutputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.validateNull;

@Slf4j
public class Responser {
    private static final String END_OF_LINE = "\r\n";
    private static final int BUFFER_SIZE = 819212312;
    private static final String CONTENT_LENGTH_HEADER = "Content-Length : ";
    private static final String DATE_HEADER = "Date : ";

    private final byte[] BUFFER = new byte[BUFFER_SIZE];
    private final InputStream contentInputStream;
    private final ContentType contentType;
    private final Status status;

    public Responser(InputStream contentInputStream, ContentType contentType, Status status) {
        validateNull(contentInputStream);
        validateNull(contentType);
        validateNull(status);

        this.contentInputStream = contentInputStream;
        this.contentType = contentType;
        this.status = status;
    }

    public static Responser create(InputStream contentInputStream, ContentType contentType, Status status) {
        validateNull(contentInputStream);
        validateNull(contentType);
        validateNull(status);

        return new Responser(contentInputStream, contentType, status);
    }


//    text 타입과 jpg 타입이 다를것 같은데..
//    text 타입의 경우 string 을 전달해줘도 되자만, jpg 의 경우 string 이 아닌 바이트로 던져줘야할것 같은데.
    public void send(OutputStream socketOutputStream) throws IOException {
        validateNull(socketOutputStream);
        BufferedOutputStream bufferedOutputStream = createBufferedOutputStream(socketOutputStream);

        while (doesNotEndOfStream(contentInputStream)) {
            int readLength = contentInputStream.read(BUFFER, 0, BUFFER_SIZE);

            byte[] response = createResponse(readLength);

            doSend(bufferedOutputStream, response);
        }
    }

    private void doSend(BufferedOutputStream bufferedOutputStream, byte[] response){
        try {
            bufferedOutputStream.write(response);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            try {
                bufferedOutputStream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    private byte[] createResponse(int readLength) {
        log.info("reda elgnt : {}", readLength);
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(status.responseLine).append(END_OF_LINE);
        responseBuilder.append(CONTENT_LENGTH_HEADER).append(readLength).append(END_OF_LINE);
        responseBuilder.append(contentType.headerContent).append(END_OF_LINE);
        responseBuilder.append(DATE_HEADER).append(new Date()).append(END_OF_LINE);
        responseBuilder.append(END_OF_LINE);
//        responseBuilder.append(new String(BUFFER, 0, readLength)).append(END_OF_LINE);

        String s = responseBuilder.toString();
        byte[] bytes = s.getBytes(UTF_8);



        byte[] responseArray = new byte[bytes.length + BUFFER.length + 10];
        System.arraycopy(bytes, 0, responseArray, 0, bytes.length);
        System.arraycopy(BUFFER, 0, responseArray, bytes.length, readLength);

        return responseArray;
    }

    private void setBody(StringBuilder responseBuilder, String content) {
        responseBuilder.append(content).append(END_OF_LINE);
    }

    // con
    private void setHeader(StringBuilder responseBuilder, int readLength) {
        responseBuilder.append(status.responseLine).append(END_OF_LINE);
        responseBuilder.append(CONTENT_LENGTH_HEADER).append(readLength).append(END_OF_LINE);
        responseBuilder.append(contentType.headerContent).append(END_OF_LINE);
        responseBuilder.append(DATE_HEADER).append(new Date()).append(END_OF_LINE);
        responseBuilder.append(END_OF_LINE);
    }

    private boolean doesNotEndOfStream(InputStream inputStream) throws IOException {
        return inputStream.available() != 0;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InputStream contentInputStream;
        private ContentType contentType;
        private Status status;

        private Builder() {
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
            return Responser.create(contentInputStream, contentType, status);
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
