package filter;

import java.util.Objects;
import java.util.Optional;

public class Filter {
    private final String name;
    private final PatternUrl patternUrl;
    private final FilterWorker filterWorker;

    public Filter(String name, PatternUrl patternUrl, FilterWorker filterWorker) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        Objects.requireNonNull(patternUrl);
        Objects.requireNonNull(filterWorker);

        this.name = name;
        this.patternUrl = patternUrl;
        this.filterWorker = filterWorker;
    }

    public String getName() {
        return name;
    }

    public FilterWorker getFilterWorker() {
        return filterWorker;
    }

    public boolean isMatchUrl(String requestUrl) {
        return matchUrl(requestUrl).isPresent();
    }

    public Optional<FilterWorker> matchUrl(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            throw new RuntimeException("requestUrl is empty.");
        }

        if (patternUrl.isMatch(requestUrl)) {
            return Optional.of(filterWorker);
        }
        return Optional.empty();
    }
}
