package filter.pattern;

import java.util.Objects;

public class WildCardFileExtensionUrl implements PatternUrl {
    private static final String PATH_DELIMITER = "/";
    private static final String FILE_DELIMITER = ".";

    private final String baseFileExtension;

    public WildCardFileExtensionUrl(String baseFileExtension) {
        if (Objects.isNull(baseFileExtension) || baseFileExtension.isBlank()) {
            throw new RuntimeException("fileExtension is emtpy.");
        }

        boolean doesNotSinglePath = baseFileExtension.contains(PATH_DELIMITER);
        if (doesNotSinglePath) {
            throw new RuntimeException("fileExtension must be single path.");
        }

        boolean doesNotExistFileDelimiter = !baseFileExtension.contains(FILE_DELIMITER);
        if (doesNotExistFileDelimiter) {
            throw new RuntimeException("file extension has file delimiter.");
        }

        this.baseFileExtension = baseFileExtension;
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

        return Objects.equals(baseFileExtension, requestFileExtension);
    }
}
