package filter;

import filter.pattern.BasePatternMatcher;
import java.util.Objects;
import java.util.Optional;

public class Filter {
    private final String name;
    private final BasePatternMatcher basePatternUrl;
    private final FilterWorker filterWorker;

    public Filter(String name, BasePatternMatcher basePatternUrl, FilterWorker filterWorker) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        Objects.requireNonNull(basePatternUrl);
        Objects.requireNonNull(filterWorker);

        this.name = name;
        this.basePatternUrl = basePatternUrl;
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

        if (basePatternUrl.isMatch(requestUrl)) {
            return Optional.of(filterWorker);
        }
        return Optional.empty();
    }
}
