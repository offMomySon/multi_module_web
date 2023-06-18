package resource;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceUrls {
    private final Set<Path> values;

    public ResourceUrls(Set<Path> values) {
        Objects.requireNonNull(values);
        this.values = values.stream()
            .filter(Objects::nonNull)
            .peek(path -> log.info("[jihun] path : {}", path))
            .collect(Collectors.toUnmodifiableSet());
    }

    public boolean contain(Path requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return false;
        }
        log.info("[jihun] requestUrl : {}", requestUrl);
        boolean contains = values.contains(requestUrl);
        log.info("[jihun] contains : {}", contains);

        return contains;
    }
}
