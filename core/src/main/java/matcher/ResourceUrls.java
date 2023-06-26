package matcher;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ResourceUrls {
    private final Set<Path> values;

    public ResourceUrls(Set<Path> values) {
        Objects.requireNonNull(values);
        this.values = values.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean contain(Path requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return false;
        }
        return values.contains(requestUrl);
    }
}
