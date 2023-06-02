package filter;

import filter.pattern.BasePatternUrl;
import filter.pattern.PatternUrl;
import filter.pattern.PatternUrls;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FilterDef {
    private final String name;
    // todo
    // filter 라는 개념아래에 patternUrl 이 n 개가 포함된 속성이라 정의한다.
    // 그렇기 때문에 name 과 pattern 이 1:1 관계로 설정하지 않고, 1:n 관계로 설정하였다.
    private final PatternUrls patternUrls;

    public FilterDef(String name, PatternUrls patternUrls) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        Objects.requireNonNull(patternUrls);

        this.name = name;
        this.patternUrls = patternUrls;
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
        PatternUrls newPatternUrls = new PatternUrls(basePatternUrls);

        return new FilterDef(name, newPatternUrls);
    }

    public Optional<String> isMatchUrl(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            throw new RuntimeException("requestUrl is empty.");
        }

        boolean match = patternUrls.isMatch(requestUrl);
        if (match) {
            return Optional.of(name);
        }
        return Optional.empty();
    }
}
