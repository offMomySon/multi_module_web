package mapper.segment;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;

public class UrlSegments {
    private static final String PATH_DELIMITER = "/";
    private static final String EMPTY_PATTERN = "";

    private final List<String> values;

    private UrlSegments(@NonNull List<String> values) {
        this.values = values;
    }

    public static UrlSegments from(String url) {
        url = Paths.get(url).normalize().toString();

        List<String> segments;
        if (Objects.equals(url, PATH_DELIMITER)) {
            segments = List.of(EMPTY_PATTERN);
        } else {
            List<String> splitThisPath = Arrays.stream(url.split(PATH_DELIMITER)).collect(Collectors.toUnmodifiableList());
            segments = splitThisPath.subList(1, splitThisPath.size());
        }

        return new UrlSegments(segments);
    }

    public List<String> getValues() {
        return values;
    }
}
