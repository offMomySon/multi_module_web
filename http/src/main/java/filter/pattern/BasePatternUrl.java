package filter.pattern;

import java.util.Objects;

public class BasePatternUrl implements PatternUrl {

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

        return baseUrl.equals(requestUrl);
    }
}
