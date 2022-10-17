package request;

import config.IpAddress;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import static java.nio.charset.StandardCharsets.UTF_8;
import static util.IoUtils.createBufferedInputStream;
import static util.ValidateUtil.*;

@Slf4j
public class ServletRequest {
    private static final int BUFFER_SIZE = 8192;
    private static final int INCREASE_BUFFER_SIZE_FACTOR = 2;

    private final HttpRequest httpRequest;
    private final IpAddress remoteAddress;

    public Uri getPath() {
        return httpRequest.getPath();
    }

    public String getQueryString() {
        return httpRequest.getQueryString();
    }

    public String getVersion() {
        return httpRequest.getVersion();
    }

    public Set<String> getHeaderKeys() {
        return httpRequest.getHeaderKeys();
    }

    public Set<String> getHeaderValue(String key) {
        return httpRequest.getHeaderValue(key);
    }

    public String getBody() {
        return httpRequest.getBody();
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public IpAddress getRemoteAddress() {
        return remoteAddress;
    }

    private ServletRequest(HttpRequest httpRequest, IpAddress remoteAddress) {
        validateNull(httpRequest);
        validateNull(remoteAddress);

        this.httpRequest = httpRequest;
        this.remoteAddress = remoteAddress;
    }

    public static ServletRequest from(InputStream inputStream, InetSocketAddress socketAddress) {
        try {
            String request = readRequest(createBufferedInputStream(inputStream));

            HttpRequest httpRequest = HttpRequest.of(request);
            IpAddress remoteAddress = IpAddress.from(socketAddress);

            return new ServletRequest(httpRequest, remoteAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readRequest(InputStream inputStream) throws IOException {
        byte[] BUFFER = new byte[BUFFER_SIZE];
        byte[] readBytes = new byte[BUFFER_SIZE];

        int nextIndex = 0;
        while (doesNotEndOfStream(inputStream)) {
            int readLength = inputStream.read(BUFFER, 0, BUFFER_SIZE);

            boolean needIncreaseBuffer = readBytes.length <= nextIndex + readLength;
            if (needIncreaseBuffer) {
                byte[] newReadBytes = Arrays.copyOf(readBytes, readBytes.length * INCREASE_BUFFER_SIZE_FACTOR);
                readBytes = newReadBytes;
            }

            System.arraycopy(BUFFER, 0, readBytes, nextIndex, readLength);
            nextIndex += readLength;
        }

        return new String(readBytes, 0, nextIndex, UTF_8);
    }

    private static boolean doesNotEndOfStream(InputStream inputStream) throws IOException {
        return inputStream.available() != 0;
    }
}
