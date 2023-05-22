package vo;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import util.IoUtils;
import static util.IoUtils.createBufferedInputStream;
import static util.IoUtils.createBufferedOutputStream;

public class HttpResponseSender implements Closeable {
    private final OutputStream outputStream;

    public HttpResponseSender(OutputStream outputStream) {
        Objects.requireNonNull(outputStream);
        this.outputStream = IoUtils.createBufferedOutputStream(outputStream);
    }

    public void send(RequestResult result) {
        Objects.requireNonNull(result);

        InputStream inputStream = result.getInputStream();

        doSend(inputStream, outputStream);
    }

    private static void doSend(InputStream inputStream, OutputStream outputStream) {
        byte[] BUFFER = new byte[8192];

        InputStream newInputStream = createBufferedInputStream(inputStream);
        OutputStream newOutputStream = createBufferedOutputStream(outputStream);

        try (inputStream) {
            int bytesRead;
            while ((bytesRead = newInputStream.read(BUFFER)) != -1) {
                newOutputStream.write(BUFFER, 0, bytesRead);
            }
            newOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
