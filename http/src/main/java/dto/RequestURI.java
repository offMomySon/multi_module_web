package dto;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import lombok.ToString;
import static validate.ValidateUtil.validateNull;

@ToString
public class RequestURI {
    private final Path value;

    private RequestURI(Path value) {
        validateNull(value);
        value = value.normalize();

        this.value = value;
    }

    public static RequestURI from(String requestUri) {
        validateNull(requestUri);

        try {
            URI uri = new URI(requestUri);
            requestUri = uri.getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(MessageFormat.format("uri syntax 가 올바르지 않습니다. e : `{}`", e));
        }

        Path path = Paths.get(requestUri);

        return new RequestURI(path);
    }
}
