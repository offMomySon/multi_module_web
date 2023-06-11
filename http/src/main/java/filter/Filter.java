package filter;

import filter.pattern.PatternMatcher;
import java.util.Objects;
import java.util.Optional;

public class Filter {
    private final String name;
    private final PatternMatcher patternMatcher;
    private final FilterWorker2 filterWorker2;

    public Filter(String name, PatternMatcher patternMatcher, FilterWorker2 filterWorker2) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        Objects.requireNonNull(patternMatcher);
        Objects.requireNonNull(filterWorker2);

        this.name = name;
        this.patternMatcher = patternMatcher;
        this.filterWorker2 = filterWorker2;
    }

    public String getName() {
        return name;
    }

    public FilterWorker2 getFilterWorker2() {
        return filterWorker2;
    }

    public PatternMatcher getPatternMatcher() {
        return patternMatcher;
    }

    public boolean isMatchUrl(String requestUrl) {
        return matchUrl(requestUrl).isPresent();
    }

    public Optional<FilterWorker2> matchUrl(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            throw new RuntimeException("requestUrl is empty.");
        }

        if (patternMatcher.isMatch(requestUrl)) {
            return Optional.of(filterWorker2);
        }
        return Optional.empty();
    }
}
