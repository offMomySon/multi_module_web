package filter.pattern;

import java.util.Objects;

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

        // todo
        // 객체의 메서드는 독립적인 맥랑을 유지해야하기 때문에 개념적인 이름을 사용해야한다.
        // 하지만 메서드 내부에서 사용되는 변수들을 객체 메서드 내부에서 컨택스트를 지닌채 사용되어야 하기 때문에
        // 단순히 연산의 결과, 수행하려는 연산을 표현하는것 보다는 컨택스트를 표현하는 이름을 사용해야 한다.
        boolean doesNotNeedCompareRequestPathCondition = basePath.length() == requestPath.length();
        if (doesNotNeedCompareRequestPathCondition) {
            return true;
        }

        boolean isRequestPathClosed = requestPath.charAt(basePath.length()) == PATH_DELIMITER;
        return isRequestPathClosed;
    }
}
