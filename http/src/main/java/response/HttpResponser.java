package response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static io.IoUtils.createBufferedInputStream;
import static io.IoUtils.createBufferedOutputStream;
import static io.IoUtils.createBufferedWriter;
import static validate.ValidateUtil.isValid;
import static validate.ValidateUtil.validateNull;

public class HttpResponser {
    private static final String END_OF_LINE = "\r\n";
    private static final String KEY_VALUE_DELIMITER = ":";
    private static final String VALUE_DELIMITER = ", ";

    private final byte[] BUFFER = new byte[8192];
    private final BufferedWriter bufferedWriter;
    private final BufferedOutputStream bufferedOutputStream;

    private String statusLine;
    private String headers;
    private BufferedInputStream bodyInputStream;

    public HttpResponser(OutputStream outputStream) {
        validateNull(outputStream);

        this.bufferedWriter = createBufferedWriter(outputStream);
        this.bufferedOutputStream = createBufferedOutputStream(outputStream);
    }

    public void send() {
        try {
            if (isValid(statusLine)) {
                String statusLine = this.statusLine + END_OF_LINE;
                bufferedWriter.write(statusLine);
            }

            if (Objects.nonNull(headers)) {
                String header = this.headers + END_OF_LINE;
                bufferedWriter.write(header);
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

    public HttpResponser responseStatus(String statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    public HttpResponser header(String headers) {
        this.headers = headers;
        return this;
    }

    public HttpResponser body(InputStream bodyInputStream) {
        if (Objects.isNull(bodyInputStream)) {
            this.bodyInputStream = null;
            return this;
        }

        this.bodyInputStream = createBufferedInputStream(bodyInputStream);
        return this;
    }
}
