package response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import static io.IoUtils.createBufferedOutputStream;
import static validate.ValidateUtil.validateNull;

public class HttpBodySender {
    private final BufferedInputStream contentStream;
    private final BufferedOutputStream bufferedOutputStream;
    private byte[] BUFFER = new byte[8192];

    public HttpBodySender(BufferedInputStream contentStream, OutputStream outputStream) {
        this.contentStream = validateNull(contentStream);
        this.bufferedOutputStream = createBufferedOutputStream(validateNull(outputStream));
    }

    public void send() {
        while (isLeftContent()) {
            try {
                int readLength = contentStream.read(BUFFER);
                bufferedOutputStream.write(BUFFER, 0, readLength);
                bufferedOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isLeftContent() {
        try {
            return contentStream.available() != 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
