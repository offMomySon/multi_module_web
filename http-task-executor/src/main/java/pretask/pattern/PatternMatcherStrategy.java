package pretask.pattern;

import filter.pattern.PatternMatcher;
import java.util.Objects;

public class PatternMatcherStrategy {
    private static final String WILD_CARD_PATH_MATTER = "/*";
    private static final String WILD_CARD_FILE_NAME = "*.";

    private final String urlPattern;

    public PatternMatcherStrategy(String urlPattern) {
        Objects.requireNonNull(urlPattern);
        if (urlPattern.isBlank()) {
            throw new RuntimeException("Does not exist urlPattern.");
        }

        this.urlPattern = urlPattern;
    }

    public PatternMatcher create() {
        if (Objects.isNull(urlPattern)) {
            throw new RuntimeException("baseUrl is null.");
        }

        if (urlPattern.startsWith(WILD_CARD_FILE_NAME)) {
            return new WildCardFileExtensionMatcher(urlPattern);
        }

        boolean wildCardPathMatch = urlPattern.endsWith(WILD_CARD_PATH_MATTER);
        if (wildCardPathMatch) {
            return new WildCardPathMatcher(urlPattern);
        }

        return new BasePatternMatcher(urlPattern);
    }
}
