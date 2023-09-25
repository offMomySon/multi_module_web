package pretask.pattern;

import java.util.Objects;

public class BasePatternMatcher implements PatternMatcher {

    private final String basePath;

    public BasePatternMatcher(String basePath) {
        if (Objects.isNull(basePath) || basePath.isBlank()) {
            throw new RuntimeException("value is empty.");
        }
        this.basePath = basePath;
    }

    @Override
    public boolean isMatch(String requestPath) {
        if (Objects.isNull(requestPath) || requestPath.isBlank()) {
            return false;
        }

        return basePath.equals(requestPath);
    }
}
