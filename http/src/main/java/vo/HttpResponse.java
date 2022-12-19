package vo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import validate.ValidateUtil;
import static io.IoUtils.createBufferedInputStream;
import static io.IoUtils.createBufferedOutputStream;
import static io.IoUtils.createBufferedWriter;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.validate;
import static validate.ValidateUtil.validateNull;

public class HttpResponse {
    private static final String START_LINE_DELIMITER = " ";
    private static final String END_OF_LINE = "\r\n";

    private final byte[] BUFFER = new byte[8192];
    private final BufferedWriter responseWriter;
    private final BufferedOutputStream responseOutputStream;

    private String httpVersion;
    private String status;
    private String statusMessage;
    private HttpHeader httpHeader;
    private BufferedInputStream sourceInputStream;

    public HttpResponse(String httpVersion, String status, String statusMessage, HttpHeader httpHeader, InputStream sourceInputStream, OutputStream responseOutputStream) {
        this.httpVersion = validate(httpVersion);
        this.status = validate(status);
        this.statusMessage = validate(statusMessage);
        this.httpHeader = validateNull(httpHeader);
        this.sourceInputStream = createBufferedInputStream(validateNull(sourceInputStream));

        validateNull(responseOutputStream);
        this.responseWriter = createBufferedWriter(responseOutputStream);
        this.responseOutputStream = createBufferedOutputStream(responseOutputStream);
    }

    public void send() {
        try {
            responseWriter.write(httpVersion);
            responseWriter.write(START_LINE_DELIMITER);
            responseWriter.write(status);
            responseWriter.write(START_LINE_DELIMITER);
            responseWriter.write(statusMessage);
            responseWriter.write(END_OF_LINE);

            responseWriter.write(httpHeader.generateHeaderMessage());
            responseWriter.write(END_OF_LINE);
            responseWriter.flush();

            if (Objects.nonNull(sourceInputStream)) {
                while (sourceInputStream.available() != 0) {
                    int read = sourceInputStream.read(BUFFER);
                    responseOutputStream.write(BUFFER, 0, read);
                }
            }

            responseOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static HttpResponse.Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private String httpVersion;
        private String status;
        private String statusMessage;
        private HttpHeader httpHeader;
        private InputStream sourceInputStream;

        private OutputStream responseOutputStream;

        private Builder() {
        }

        public Builder httpVersion(String httpVersion) {
            validate(httpVersion);
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder status(String status) {
            validate(status);
            this.status = status;
            return this;
        }

        public Builder statusMessage(String statusMessage) {
            validate(statusMessage);
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder httpHeader(HttpHeader httpHeader) {
            validateNull(httpHeader);
            this.httpHeader = httpHeader;
            return this;
        }

        public Builder sourceInputStream(InputStream sourceInputStream) {
            if (Objects.isNull(sourceInputStream)) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("".getBytes(UTF_8));
                this.sourceInputStream = byteArrayInputStream;
                return this;
            }

            this.sourceInputStream = sourceInputStream;
            return this;
        }

        public Builder responseOutputStream(OutputStream responseOutputStream){
            validateNull(responseOutputStream);
            this.responseOutputStream = responseOutputStream;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this.httpVersion, this.status, this.statusMessage, this.httpHeader, this.sourceInputStream, this.responseOutputStream);
        }
    }
}
