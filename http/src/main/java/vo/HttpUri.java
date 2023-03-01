package vo;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Objects;
import lombok.ToString;
import validate.ValidateUtil;

@ToString
public class HttpUri {
    private final Path value;

    private HttpUri(Path value) {
        ValidateUtil.validateNull(value);
        value = value.normalize();

        this.value = value;
    }

    public static HttpUri from(String requestUri) {
        ValidateUtil.validateNull(requestUri);

        try {
            URI uri = new URI(requestUri);
            requestUri = uri.getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(MessageFormat.format("uri syntax 가 올바르지 않습니다. e : `{0}`", e));
        }

        Path path = Paths.get(requestUri);

        return new HttpUri(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpUri httpUri = (HttpUri) o;
        return value.equals(httpUri.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
