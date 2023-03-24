package mapper;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import vo.HttpMethod;

public class HttpPathResolver {
    private static final String PATH_DELIMITER = "/";
    private static final String EMPTY_PATTERN = "";

    private final HttpMethod httpMethod;
    private final String url;
    private final Method javaMethod;

    public HttpPathResolver(HttpMethod httpMethod, String url, Method javaMethod) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.javaMethod = javaMethod;
    }

    public Optional<ResolvedMethod> resolveMethod(HttpMethod requestMethod, String requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return Optional.empty();
        }
        if (httpMethod != requestMethod) {
            return Optional.empty();
        }

        Map<String, String> pathVariables = new HashMap<>();
        if (doesNotMatch(requestUrl, pathVariables)) {
            return Optional.empty();
        }

        ResolvedMethod resolvedMethod = new ResolvedMethod(javaMethod, pathVariables);
        return Optional.of(resolvedMethod);
    }

    private boolean doesNotMatch(String requestUrl, Map<String, String> pathVariables) {
        return !match(requestUrl, pathVariables);
    }

    private boolean match(String requestUrl, Map<String, String> pathVariables) {
        requestUrl = Paths.get(requestUrl).normalize().toString();

        List<String> thisPaths;
        if (Objects.equals(this.url, PATH_DELIMITER)) {
            thisPaths = List.of(EMPTY_PATTERN);
        } else {
            List<String> splitThisPath = Arrays.stream(this.url.split(PATH_DELIMITER)).collect(Collectors.toUnmodifiableList());
            thisPaths = splitThisPath.subList(1, splitThisPath.size());
        }

        List<String> requestPaths;
        if (Objects.equals(requestUrl, PATH_DELIMITER)) {
            requestPaths = List.of(EMPTY_PATTERN);
        } else {
            List<String> splitRequestPaths = Arrays.stream(requestUrl.split(PATH_DELIMITER)).collect(Collectors.toUnmodifiableList());
            requestPaths = splitRequestPaths.subList(1, splitRequestPaths.size());
        }

        return doMatch(thisPaths, requestPaths, 0, 0, pathVariables);
    }

    private boolean doMatch(List<String> thisPaths, List<String> requestPaths, int thisIndex, int requestIndex, Map<String, String> pathVariables) {
        if (PathUtils.outOfIndex(thisPaths, thisIndex) && PathUtils.outOfIndex(requestPaths, requestIndex)) {
            return true;
        }
        if (PathUtils.outOfIndex(requestPaths, requestIndex)) {
            return PathUtils.onlyRemainWildCard(thisPaths, thisIndex);
        }
        if (PathUtils.outOfIndex(thisPaths, thisIndex)) {
            return false;
        }

        Path thisPath = new Path(thisPaths.get(thisIndex));
        Path requestPath = new Path(requestPaths.get(requestIndex));

        if (thisPath.match(requestPath)) {
            return doMatch(thisPaths, requestPaths, thisIndex + 1, requestIndex + 1, pathVariables);
        }

        if (thisPath.isPathVariable()) {
            boolean emptyRequestPath = requestPath.isEmpty();
            if (emptyRequestPath) {
                return false;
            }

            String key = thisPath.removeBraces();
            String value = requestPath.getValue();
            pathVariables.put(key, value);

            boolean doMatch = doMatch(thisPaths, requestPaths, thisIndex + 1, requestIndex + 1, pathVariables);

            if (!doMatch) {
                pathVariables.remove(key);
            }

            return doMatch;
        }

        if (thisPath.doesNotWildCard()) {
            return false;
        }

        if (PathUtils.onlyRemainWildCard(thisPaths, thisIndex)) {
            return true;
        }

        List<Integer> nextRequestIndexes = PathUtils.getIndexesFromStartToEnd(requestPaths, requestIndex);
        return nextRequestIndexes.stream()
            .anyMatch(_nextRequestIndex -> doMatch(thisPaths, requestPaths, thisIndex + 1, _nextRequestIndex, pathVariables));
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