package vo;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Objects;

public class HttpRequestPath {
    private final Path value;

    private HttpRequestPath(Path value) {
        Objects.requireNonNull(value);

        value = value.normalize();

        this.value = value;
    }

    public Path getValue() {
        return Path.of(value.toString());
    }

    public static HttpRequestPath from(String requestUri) {
        Objects.requireNonNull(requestUri);

        try {
            URI uri = new URI(requestUri);
            requestUri = uri.getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(MessageFormat.format("uri syntax 가 올바르지 않습니다. e : `{0}`", e));
        }

        Path path = Paths.get(requestUri);

        return new HttpRequestPath(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequestPath httpRequestPath = (HttpRequestPath) o;
        return value.equals(httpRequestPath.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "HttpRequestPath{" +
            "value=" + value +
            '}';
    }
}
