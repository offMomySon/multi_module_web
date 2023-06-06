package filter.pattern;

import java.util.Objects;

public class PatternMatcherStrategy {
    private static final String WILD_CARD_PATH_MATTER = "/*";
    private static final String WILD_CARD_FILE_NAME = "*.";

    public static PatternMatcher create(String basePath) {
        if (Objects.isNull(basePath)) {
            throw new RuntimeException("baseUrl is null.");
        }

        if (basePath.startsWith(WILD_CARD_FILE_NAME)) {
            return new WildCardFileExtensionMatcher(basePath);
        }

        boolean wildCardPathMatch = basePath.endsWith(WILD_CARD_PATH_MATTER);
        if (wildCardPathMatch) {
            return new WildCardPathMatcher(basePath);
        }

        return new BasePatternMatcher(basePath);
    }
}
