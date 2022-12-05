package response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import validate.ValidateUtil;
import static io.IoUtils.createBufferedInputStream;
import static io.IoUtils.createBufferedOutputStream;
import static io.IoUtils.createBufferedWriter;
import static validate.ValidateUtil.validate;
import static validate.ValidateUtil.validateNull;

public class HttpResponser {
    private static final String END_OF_LINE = "\r\n";

    private final byte[] BUFFER = new byte[8192];
    private final BufferedWriter bufferedWriter;
    private final BufferedOutputStream bufferedOutputStream;

    private String startLine;
    private String header;
    private BufferedInputStream bodyInputStream;

    public HttpResponser(OutputStream outputStream) {
        validateNull(outputStream);

        this.bufferedWriter = createBufferedWriter(outputStream);
        this.bufferedOutputStream = createBufferedOutputStream(outputStream);
    }

    public void send() {
        try {
            if (Objects.nonNull(startLine)) {
                bufferedWriter.write(startLine);
            }
            if (Objects.nonNull(header)) {
                bufferedWriter.write(header);
            }

            bufferedWriter.write(END_OF_LINE);

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

    public HttpResponser startLine(String startLine) {
        validate(startLine);
        this.startLine = startLine;
        return this;
    }

    public HttpResponser header(String header) {
        validate(header);
        this.header = header;
        return this;
    }

    public HttpResponser body(InputStream bodyInputStream) {
        validateNull(bodyInputStream);
        this.bodyInputStream = createBufferedInputStream(bodyInputStream);
        return this;
    }
}
