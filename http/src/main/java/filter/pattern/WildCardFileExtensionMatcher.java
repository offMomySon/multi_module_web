package filter.pattern;

import java.util.Objects;

public class WildCardFileExtensionMatcher implements PatternMatcher {
    private static final String FILE_NAME_WILD_CARD = "*.";

    private static final String PATH_DELIMITER = "/";
    private static final String FILE_DELIMITER = ".";

    private final String fileExtension;

    public WildCardFileExtensionMatcher(String fileExtension) {
        if (Objects.isNull(fileExtension) || fileExtension.isBlank()) {
            throw new RuntimeException("fileExtension is emtpy.");
        }

        boolean doesNotSinglePath = fileExtension.contains(PATH_DELIMITER);
        if (doesNotSinglePath) {
            throw new RuntimeException("fileExtension must be single path.");
        }

        boolean doesNotStartWithWildCard = !fileExtension.startsWith(FILE_NAME_WILD_CARD);
        if (doesNotStartWithWildCard) {
            throw new RuntimeException("does not start with wild card.");
        }

        int fileExtensionIndex = fileExtension.indexOf(FILE_NAME_WILD_CARD) + FILE_NAME_WILD_CARD.length() - 1;
        this.fileExtension = fileExtension.substring(fileExtensionIndex);
    }

    @Override
    public boolean isMatch(String requestPath) {
        if (Objects.isNull(requestPath) || requestPath.isBlank()) {
            return false;
        }

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

        String requestFileExtension = requestPath.substring(fileDelimiterIndex);

        return Objects.equals(fileExtension, requestFileExtension);
    }
}
