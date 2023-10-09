package vo;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponseWriter implements Closeable {

    private final HttpResponse httpResponse;

    public HttpResponseWriter(HttpResponse httpResponse) {
        Objects.requireNonNull(httpResponse);
        this.httpResponse = httpResponse;
    }

    public void send(String body) {
        Objects.requireNonNull(body);

        ByteArrayInputStream bodyStream = new ByteArrayInputStream(body.getBytes(UTF_8));

        httpResponse.send(bodyStream);
        httpResponse.flush();
    }

    public void send(char body) {
        Objects.requireNonNull(body);

        byte[] newBody = Character.toString(body).getBytes(UTF_8);
        ByteArrayInputStream bodyStream = new ByteArrayInputStream(newBody);

        httpResponse.send(bodyStream);
        httpResponse.flush();
    }

    public void send(Integer body) {
        Objects.requireNonNull(body);

        byte[] newBody = ByteBuffer.allocate(4).putInt(body).array();
        ByteArrayInputStream bodyStream = new ByteArrayInputStream(newBody);

        httpResponse.send(bodyStream);
        httpResponse.flush();
    }

    public void send(InputStream body) {

        httpResponse.send(body);
        httpResponse.flush();
    }

    public void flush() {
        httpResponse.flush();
    }

    @Override
    public void close() throws IOException {
        httpResponse.close();
    }
}
