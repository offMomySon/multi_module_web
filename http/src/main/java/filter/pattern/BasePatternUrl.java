package filter.pattern;

import java.util.Objects;

public class BasePatternUrl implements PatternUrl {
    private static final String PATH_WILD_CARD = "/*";
    private static final char PATH_DELIMITER = '/';
    private static final String FILE_NAME_WILD_CARD = "*.";
    private static final char FILE_DELIMITER = '.';

    private final String baseUrl;

    public BasePatternUrl(String baseUrl) {
        if (Objects.isNull(baseUrl) || baseUrl.isBlank()) {
            throw new RuntimeException("value is empty.");
        }
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean isMatch(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            return false;
        }
        return doMatch(this.baseUrl, requestUrl);
    }

    private static boolean doMatch(String basePath, String requestPath) {
        boolean isAllMatchPath = basePath.equals(PATH_WILD_CARD);
        if (isAllMatchPath) {
            return true;
        }

        boolean isPathEqual = basePath.equals(requestPath);
        if (isPathEqual) {
            return true;
        }

        boolean lastAllMatchPath = basePath.endsWith(PATH_WILD_CARD);
        if (lastAllMatchPath) {
            int wildCardExcludeLength = basePath.length() - PATH_WILD_CARD.length();

            // todo
            // 표현하고자 하는 관점인 wild card exclude Length 에 따른 sub path 인것은 잘 표한하였다.
            // 또한 잘 나타나는 것 같지만, 변수가 너무길다.
            // 나에게는 잘 읽히는데 다른사람에게는 어떻게 읽히게 될까?
            String wildCardExcludeBasePath = basePath.substring(0, wildCardExcludeLength);
            String wildCardExcludeLengthRequestPath = requestPath.substring(0, wildCardExcludeLength);

            boolean doesNotMatch = !Objects.equals(wildCardExcludeBasePath, wildCardExcludeLengthRequestPath);
            if (doesNotMatch) {
                return false;
            }

            // todo
            // 객체의 메서드는 독립적인 맥랑을 유지해야하기 때문에 개념적인 이름을 사용해야한다.
            // 하지만 메서드 내부에서 사용되는 변수들을 객체 메서드 내부에서 컨택스트를 지닌채 사용되어야 하기 때문에
            // 단순히 연산의 결과, 수행하려는 연산을 표현하는것 보다는 컨택스트를 표현하는 이름을 사용해야 한다.
            boolean alreadyMatchCompared = wildCardExcludeBasePath.length() == requestPath.length();
            if (alreadyMatchCompared) {
                return true;
            }

            boolean doesNotRequestPathEndChar = requestPath.charAt(wildCardExcludeLength) != PATH_DELIMITER;
            if (doesNotRequestPathEndChar) {
                return false;
            }
            return true;
        }

        boolean fileNameWildCard = basePath.startsWith(FILE_NAME_WILD_CARD);
        if (fileNameWildCard) {
            int pathDelimiterIndex = requestPath.lastIndexOf(PATH_DELIMITER);
            int fileDelimiterIndex = requestPath.lastIndexOf(FILE_DELIMITER);

            boolean doesNotExistDelimiter = pathDelimiterIndex == -1 || fileDelimiterIndex == -1;
            if (doesNotExistDelimiter) {
                return false;
            }
            boolean doesNotFileDelimiterRightThanPathDelimiter = pathDelimiterIndex >= fileDelimiterIndex;
            if (doesNotFileDelimiterRightThanPathDelimiter) {
                return false;
            }

            int baseFileDelimiterIndex = basePath.lastIndexOf(FILE_DELIMITER);
            String baseFileExtension = basePath.substring(baseFileDelimiterIndex);
            String requestFileExtension = requestPath.substring(fileDelimiterIndex);

            return Objects.equals(baseFileExtension, requestFileExtension);
        }

        return false;
    }
}
