package response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import static io.IoUtils.createBufferedInputStream;
import static io.IoUtils.createBufferedOutputStream;
import static io.IoUtils.createBufferedWriter;
import static validate.ValidateUtil.isValid;
import static validate.ValidateUtil.validateNull;

public class HttpResponse {
    private static final String END_OF_LINE = "\r\n";

    private final byte[] BUFFER = new byte[8192];
    private final BufferedWriter bufferedWriter;
    private final BufferedOutputStream bufferedOutputStream;

    private String statusLine;
    private String headers;
    private BufferedInputStream bodyInputStream;

    public HttpResponse(OutputStream outputStream) {
        validateNull(outputStream);

        this.bufferedWriter = createBufferedWriter(outputStream);
        this.bufferedOutputStream = createBufferedOutputStream(outputStream);
    }

    public void send() {
        try {
            if (isValid(statusLine)) {
                bufferedWriter.write(statusLine);
            }

            if (Objects.nonNull(headers)) {
                bufferedWriter.write(headers);
            }

            bufferedWriter.write(END_OF_LINE);
            bufferedWriter.flush();

            if (Objects.nonNull(bodyInputStream)) {
                while (bodyInputStream.available() != 0) {
                    int read = bodyInputStream.read(BUFFER);
                    bufferedOutputStream.write(BUFFER, 0, read);
                }
            }

            bufferedOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse responseStatus(String statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    public HttpResponse header(String headers) {
        this.headers = headers;
        return this;
    }

    public HttpResponse body(InputStream bodyInputStream) {
        if (Objects.isNull(bodyInputStream)) {
            this.bodyInputStream = null;
            return this;
        }

        this.bodyInputStream = createBufferedInputStream(bodyInputStream);
        return this;
    }
}
