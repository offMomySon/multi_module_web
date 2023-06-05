package filter;

import filter.pattern.BasePatternUrl;
import filter.pattern.CompositePatternUrl;
import filter.pattern.PatternUrl;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FilterDef {
    private final String name;
    private final CompositePatternUrl compositePatternUrl;

    public FilterDef(String name, CompositePatternUrl compositePatternUrl) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        Objects.requireNonNull(compositePatternUrl);

        this.name = name;
        this.compositePatternUrl = compositePatternUrl;
    }

    public static FilterDef of(String name, List<String> patternUrls) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        Objects.requireNonNull(patternUrls);
        patternUrls = patternUrls.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());

        List<PatternUrl> basePatternUrls = patternUrls.stream()
            .map(BasePatternUrl::new)
            .collect(Collectors.toUnmodifiableList());
        CompositePatternUrl newCompositePatternUrl = new CompositePatternUrl(basePatternUrls);

        return new FilterDef(name, newCompositePatternUrl);
    }

    public Optional<String> matchUrl(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            throw new RuntimeException("requestUrl is empty.");
        }

        boolean match = compositePatternUrl.isMatch(requestUrl);
        if (match) {
            return Optional.of(name);
        }
        return Optional.empty();
    }
}
