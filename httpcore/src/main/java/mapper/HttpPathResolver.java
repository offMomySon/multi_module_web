package mapper;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import vo.HttpMethod;

public class HttpPathResolver {
    private static final String PATH_DELIMITER = "/";
    private static final String WILD_CARD = "**";
    private static final String EMPTY_PATTERN = "";
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";

    private final HttpMethod httpMethod;
    private final String url;
    private final Method javaMethod;

    public HttpPathResolver(HttpMethod httpMethod, String url, Method javaMethod) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.javaMethod = javaMethod;
    }

    public Optional<Method> resolve(HttpMethod requestMethod, String requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return Optional.empty();
        }
        if (httpMethod != requestMethod) {
            return Optional.empty();
        }
        if (doesNotMatch(requestUrl)) {
            return Optional.empty();
        }

        return Optional.of(javaMethod);
    }

    private boolean doesNotMatch(String requestUrl) {
        return !match(requestUrl);
    }

    private boolean match(String requestUrl) {
        requestUrl = Paths.get(requestUrl).normalize().toString();

        List<String> thisPaths;
        if (Objects.equals(this.url, "/")) {
            thisPaths = List.of(EMPTY_PATTERN);
        } else {
            List<String> splitThisPath = Arrays.stream(this.url.split(PATH_DELIMITER)).collect(Collectors.toUnmodifiableList());
            thisPaths = splitThisPath.subList(1, splitThisPath.size());
        }

        List<String> requestPaths;
        if (Objects.equals(requestUrl, "/")) {
            requestPaths = List.of(EMPTY_PATTERN);
        } else {
            List<String> splitRequestPaths = Arrays.stream(requestUrl.split(PATH_DELIMITER)).collect(Collectors.toUnmodifiableList());
            requestPaths = splitRequestPaths.subList(1, splitRequestPaths.size());
        }

        return doMatch(thisPaths, requestPaths, 0, 0);
    }

    private boolean doMatch(List<String> thisPaths, List<String> requestPaths, int thisIndex, int requestIndex) {
        boolean finishMatch = PathUtils.outOfIndex(thisPaths, thisIndex) && PathUtils.outOfIndex(requestPaths, requestIndex);
        if (finishMatch) {
            return true;
        }

        boolean onlyRemainThisPaths = PathUtils.outOfIndex(requestPaths, requestIndex);
        if (onlyRemainThisPaths) {
            boolean onlyRemainWildCard = PathUtils.onlyRemainWildCard(thisPaths, thisIndex);
            return onlyRemainWildCard;
        }

        boolean onlyRemainRequestPaths = PathUtils.outOfIndex(thisPaths, thisIndex);
        if (onlyRemainRequestPaths) {
            return false;
        }

        String thisPath = thisPaths.get(thisIndex);
        String requestPath = requestPaths.get(requestIndex);
        int nextThisIndex = thisIndex + 1;

        boolean match = PathUtils.matchPattern(thisPath, requestPath);
        if (match) {
            int nextRequestIndex = requestIndex + 1;
            return doMatch(thisPaths, requestPaths, nextThisIndex, nextRequestIndex);
        }

        boolean pathVariable = PathUtils.isPathVariable(thisPath);
        if (pathVariable) {
            boolean emptyPatternRequestPath = PathUtils.emptyPattern(requestPath);
            if (emptyPatternRequestPath) {
                return false;
            }
            int nextRequestIndex = requestIndex + 1;
            return doMatch(thisPaths, requestPaths, nextThisIndex, nextRequestIndex);
        }

        boolean doesNotWildCard = PathUtils.doesNotWildCardPattern(thisPath);
        if (doesNotWildCard) {
            return false;
        }

        boolean onlyRemainWildCard = PathUtils.onlyRemainWildCard(thisPaths, thisIndex);
        if (onlyRemainWildCard) {
            return true;
        }

        List<Integer> nextRequestIndexes = PathUtils.getBehindeIndexes(requestPaths, requestIndex);
        return nextRequestIndexes.stream()
            .anyMatch(_nextRequestIndex -> doMatch(thisPaths, requestPaths, nextThisIndex, _nextRequestIndex));
    }

    @Getter
    public static class ResolvedMethod {
        private final Method javaMethod;
        private final Map<String, String> pathVariable;

        public ResolvedMethod(Method javaMethod, Map<String, String> pathVariable) {
            this.javaMethod = javaMethod;
            this.pathVariable = pathVariable;
        }
    }
}