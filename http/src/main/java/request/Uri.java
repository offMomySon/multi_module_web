package request;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.net.URLCodec;
import static validate.ValidateUtil.validate;
import static validate.ValidateUtil.validateNull;

@Slf4j
@ToString
public class Uri {
    private static String QUERY_DELIMITER = "\\?";

    private final Path value;

    public Path getValue() {
        return value;
    }

    private Uri(Path path) {
        validateNull(path);

        path = path.normalize();

        String[] splitUri = path.toString().split(QUERY_DELIMITER, 2);
        String uri = splitUri[0];

        this.value = path;
    }

    @JsonCreator
    private static Uri ofJackson(String _path) {
        return from(_path);
    }

    public static Uri from(String _path){
        validate(_path);

        try {
            URI uri = new URI(_path);
            System.out.println(uri.toASCIIString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Path path = Paths.get(_path);

        return new Uri(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Uri uri = (Uri) o;
        return Objects.equals(value, uri.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
