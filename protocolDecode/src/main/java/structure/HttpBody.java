package structure;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import static io.IoUtils.createBufferedInputStream;
import static validate.ValidateUtil.validateNull;

public class HttpBody {
    private static final byte[] BUFFER = new byte[8192];
    private final Integer contentLength;
    private final BufferedInputStream bufferedInputStream;
    private Integer readLength = 0;

    private HttpBody(Integer contentLength, BufferedInputStream bufferedInputStream) {
        this.contentLength = validateNull(contentLength);
        this.bufferedInputStream = validateNull(bufferedInputStream);
    }

    public static HttpBody from(Integer contentLength, InputStream inputStream) {
        validateNull(contentLength);
        validateNull(inputStream);

        return new HttpBody(contentLength, createBufferedInputStream(inputStream));
    }

    public byte[] read() {
        try {
            int read = bufferedInputStream.read(BUFFER);
            if (read == 0) {
                throw new RuntimeException("Not left stream.");
            }
            readLength += read;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return BUFFER;
    }

    public boolean isLeftStream() {
        return readLength != contentLength;
    }
}
