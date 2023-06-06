package filter.pattern;

import java.util.Objects;

public class PatternUrlStrategy {
    private static final String WILD_CARD_PATH_MATTER = "/*";
    private static final String WILD_CARD_FILE_NAME = "*.";

    public static PatternUrl create(String basePath) {
        if (Objects.isNull(basePath)) {
            throw new RuntimeException("baseUrl is null.");
        }

        if (basePath.startsWith(WILD_CARD_FILE_NAME)) {
            return new WildCardFileExtensionUrl(basePath);
        }

        boolean wildCardPathMatch = basePath.endsWith(WILD_CARD_PATH_MATTER);
        if (wildCardPathMatch) {
            return new WildCardPathUrl(basePath);
        }

        return new BasePatternUrl(basePath);
    }
}
