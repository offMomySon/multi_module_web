package request;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import lombok.NonNull;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ServletRequest implements Closeable {
    private static final int BUFFER_SIZE = 8192;

    private final BufferedInputStream bufferedInputStream;
    private final byte[] BUFFER = new byte[BUFFER_SIZE];

    private ServletRequest(@NonNull BufferedInputStream bufferedInputStream) {
        this.bufferedInputStream = bufferedInputStream;
    }

    public static ServletRequest from(Socket socket) {
        try {
            return new ServletRequest(new BufferedInputStream(socket.getInputStream(), BUFFER_SIZE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpRequest readHttpRequest() {
        try {
            return HttpRequest.of(readRequest());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readRequest() throws IOException {
        StringBuilder requestBuilder = new StringBuilder();
        while (doesNotEndOfStream(bufferedInputStream)) {
            int readLength = bufferedInputStream.read(BUFFER, 0, BUFFER_SIZE);
            String partOfRequest = new String(BUFFER, 0, readLength, UTF_8);

            requestBuilder.append(partOfRequest);
        }

        return requestBuilder.toString();
    }

    private static boolean doesNotEndOfStream(BufferedInputStream bufferedInputStream) throws IOException {
        return bufferedInputStream.available() != 0;
    }

    @Override
    public void close() throws IOException {
        bufferedInputStream.close();
    }
}
