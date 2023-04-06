package vo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import util.IoUtils;
import static java.nio.charset.StandardCharsets.UTF_8;

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
        if (StringUtils.isEmpty(httpVersion) || StringUtils.isBlank(httpVersion)) {
            throw new RuntimeException(MessageFormat.format("httpVersion is invalid : `{}`", httpVersion));
        }
        if (StringUtils.isEmpty(status) || StringUtils.isBlank(status)) {
            throw new RuntimeException(MessageFormat.format("status is invalid : `{}`", status));
        }
        if (StringUtils.isEmpty(statusMessage) || StringUtils.isBlank(statusMessage)) {
            throw new RuntimeException(MessageFormat.format("statusMessage is invalid : `{}`", statusMessage));
        }
        Objects.requireNonNull(httpHeader, "httpHeader is null.");
        Objects.requireNonNull(sourceInputStream, "sourceInputStream is null.");
        Objects.requireNonNull(responseOutputStream, "responseOutputStream is null.");

        this.httpVersion = httpVersion;
        this.status = status;
        this.statusMessage = statusMessage;
        this.httpHeader = httpHeader;
        this.sourceInputStream = IoUtils.createBufferedInputStream(sourceInputStream);
        this.responseWriter = IoUtils.createBufferedWriter(responseOutputStream);
        this.responseOutputStream = IoUtils.createBufferedOutputStream(responseOutputStream);
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
            if (StringUtils.isEmpty(httpVersion) || StringUtils.isBlank(httpVersion)) {
                throw new RuntimeException(MessageFormat.format("httpVersion is invalid : `{}`", httpVersion));
            }
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder status(String status) {
            if (StringUtils.isEmpty(status) || StringUtils.isBlank(status)) {
                throw new RuntimeException(MessageFormat.format("status is invalid : `{}`", status));
            }
            this.status = status;
            return this;
        }

        public Builder statusMessage(String statusMessage) {
            if (StringUtils.isEmpty(statusMessage) || StringUtils.isBlank(statusMessage)) {
                throw new RuntimeException(MessageFormat.format("statusMessage is invalid : `{}`", statusMessage));
            }
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder httpHeader(HttpHeader httpHeader) {
            Objects.requireNonNull(httpHeader, "httpHeader is null.");
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
            Objects.requireNonNull(responseOutputStream);
            this.responseOutputStream = responseOutputStream;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this.httpVersion, this.status, this.statusMessage, this.httpHeader, this.sourceInputStream, this.responseOutputStream);
        }
    }
}
