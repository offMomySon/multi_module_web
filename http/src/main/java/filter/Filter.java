package filter;

import filter.pattern.PatternMatcher;
import java.util.Objects;
import java.util.Optional;

public class Filter {
    private final String name;
    private final PatternMatcher patternMatcher;
    private final FilterWorker filterWorker;

    public Filter(String name, PatternMatcher patternMatcher, FilterWorker filterWorker) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        Objects.requireNonNull(patternMatcher);
        Objects.requireNonNull(filterWorker);

        this.name = name;
        this.patternMatcher = patternMatcher;
        this.filterWorker = filterWorker;
    }

    public String getName() {
        return name;
    }

    public FilterWorker getFilterWorker2() {
        return filterWorker;
    }

    public PatternMatcher getPatternMatcher() {
        return patternMatcher;
    }

    public boolean isMatchUrl(String requestUrl) {
        return matchUrl(requestUrl).isPresent();
    }

    public Optional<FilterWorker> matchUrl(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            throw new RuntimeException("requestUrl is empty.");
        }

        if (patternMatcher.isMatch(requestUrl)) {
            return Optional.of(filterWorker);
        }
        return Optional.empty();
    }
}
