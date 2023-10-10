package pretask.pattern;

import java.util.Objects;
import task.pattern.PatternMatcher;

public class WildCardPathMatcher implements PatternMatcher {
    private static final String WILD_CARD = "/*";
    private static final String EMPTY_URL = "";
    private static final char PATH_DELIMITER = '/';

    private final String basePath;

    public WildCardPathMatcher(String basePath) {
        if (Objects.isNull(basePath) || basePath.isBlank()) {
            throw new RuntimeException("baseUrl is empty.");
        }

        boolean doesNotEndWithWildCard = !basePath.endsWith(WILD_CARD);
        if (doesNotEndWithWildCard) {
            throw new RuntimeException("does not end of wild card");
        }


        String newBaseUrl = EMPTY_URL;
        int indexOfWildCard = basePath.indexOf(WILD_CARD);
        if (indexOfWildCard > 0) {
            newBaseUrl = basePath.substring(0, indexOfWildCard);
        }

        this.basePath = newBaseUrl;
    }

    @Override
    public boolean isMatch(String requestPath) {
        if (Objects.isNull(requestPath) || requestPath.isBlank()) {
            return false;
        }

        boolean doesNotEnoughLength = basePath.length() > requestPath.length();
        if (doesNotEnoughLength) {
            return false;
        }

        boolean isEmptyBaseUrl = Objects.equals(basePath, EMPTY_URL);
        if (isEmptyBaseUrl) {
            return true;
        }

        String subRequestPath = requestPath.substring(0, basePath.length());

        boolean doesNotMatch = !Objects.equals(basePath, subRequestPath);
        if (doesNotMatch) {
            return false;
        }

        boolean doesNotNeedCompareRequestPathCondition = basePath.length() == requestPath.length();
        if (doesNotNeedCompareRequestPathCondition) {
            return true;
        }

        boolean isRequestPathClosed = requestPath.charAt(basePath.length()) == PATH_DELIMITER;
        return isRequestPathClosed;
    }
}
