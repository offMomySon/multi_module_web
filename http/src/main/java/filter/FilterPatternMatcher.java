package filter;

import filter.pattern.PatternUrl;
import java.util.Objects;
import java.util.Optional;

public class FilterPatternMatcher {
    private final String filerName;
    private final PatternUrl patternUrl;

    public FilterPatternMatcher(String filerName, PatternUrl patternUrl) {
        if (Objects.isNull(filerName) || filerName.isBlank()) {
            throw new RuntimeException("filtername is empty.");
        }
        Objects.requireNonNull(patternUrl);

        this.filerName = filerName;
        this.patternUrl = patternUrl;
    }

    public Optional<String> match(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            return Optional.empty();
        }

        boolean match = patternUrl.isMatch(requestUrl);
        if (match) {
            return Optional.of(filerName);
        }
        return Optional.empty();
    }
}
