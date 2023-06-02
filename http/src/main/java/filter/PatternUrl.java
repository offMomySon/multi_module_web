package filter;

import java.util.Objects;

public class PatternUrl {

    private final String baseUrl;

    public PatternUrl(String baseUrl) {
        if (Objects.isNull(baseUrl) || baseUrl.isBlank()) {
            throw new RuntimeException("value is empty.");
        }
        this.baseUrl = baseUrl;
    }

    public boolean isMatch(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            return false;
        }
        return doMatch(this.baseUrl, requestUrl);
    }

    private static boolean doMatch(String testPath, String requestPath) {
        // Case 1 - Exact Match
        if (testPath.equals(requestPath)) {
            return true;
        }

        // Case 2 - Path Match ("/.../*")
        if (testPath.equals("/*")) {
            return true;
        }
        if (testPath.endsWith("/*")) {
            if (testPath.regionMatches(0, requestPath, 0, testPath.length() - 2)) {
                if (requestPath.length() == (testPath.length() - 2)) {
                    return true;
                } else if ('/' == requestPath.charAt(testPath.length() - 2)) {
                    return true;
                }
            }
            return false;
        }

        // Case 3 - Extension Match
        if (testPath.startsWith("*.")) {
            int slash = requestPath.lastIndexOf('/');
            int period = requestPath.lastIndexOf('.');
            if ((slash >= 0) && (period > slash) && (period != requestPath.length() - 1) &&
                ((requestPath.length() - period) == (testPath.length() - 1))) {
                return testPath.regionMatches(2, requestPath, period + 1, testPath.length() - 2);
            }
        }

        // Case 4 - "Default" Match
        return false; // NOTE - Not relevant for selecting filters
    }
}
