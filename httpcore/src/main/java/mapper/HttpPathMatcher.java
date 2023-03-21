package mapper;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import lombok.NonNull;
import vo.HttpMethod;

public class HttpPathMatcher {
    private static final String PATH_DELIMITER = "/";
    private static final String WILD_CARD = "**";

    private final HttpMethod httpMethod;
    private final String url;
    private final Method javaMethod;

    public HttpPathMatcher(@NonNull HttpMethod httpMethod, @NonNull String url, @NonNull Method javaMethod) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.javaMethod = javaMethod;
    }

    public Optional<Result> match(HttpMethod httpMethod, String otherUrl) {
        if (Objects.isNull(otherUrl) || otherUrl.isEmpty() || otherUrl.isBlank()) {
            return Optional.empty();
        }

        if (this.httpMethod != httpMethod) {
            return Optional.empty();
        }

        Map<String, String> pathVariables = new HashMap<>();
        if (doesNotMatch(otherUrl, pathVariables)) {
            return Optional.empty();
        }

        Result result = new Result(javaMethod, pathVariables);
        return Optional.of(result);
    }

    private boolean doesNotMatch(String otherUrl, Map<String, String> pathVariables) {
        return !match(otherUrl, pathVariables);
    }

    private boolean match(String otherUrl, Map<String, String> pathVariables) {
        if (Objects.isNull(otherUrl) || otherUrl.isEmpty() || otherUrl.isBlank()) {
            return false;
        }

        if (!otherUrl.startsWith("/")) {
            otherUrl = "/" + otherUrl;
        }

        otherUrl = URI.create(otherUrl).getPath();
        otherUrl = Paths.get(otherUrl).normalize().toString();

        String[] splitThisPaths = this.url.split(PATH_DELIMITER);
        String[] splitOtherPahts = otherUrl.split(PATH_DELIMITER);

        Queue<String> thisPaths = new ArrayDeque<>();
        Queue<String> otherPaths = new ArrayDeque<>();
        for (String thisPath : splitThisPaths) {
            thisPaths.offer(thisPath);
        }
        for (String otherPath : splitOtherPahts) {
            otherPaths.offer(otherPath);
        }

        return doMatch(thisPaths, otherPaths, pathVariables);
    }

    private boolean doMatch(Queue<String> thisPaths, Queue<String> otherPaths, Map<String, String> pathVariables) {

        while (!thisPaths.isEmpty() && !otherPaths.isEmpty()) {
            String thisPath = thisPaths.peek();
            String otherPath = otherPaths.peek();

            if (Objects.equals(thisPath, otherPath)) {
                thisPaths.poll();
                otherPaths.poll();
                continue;
            }

            if (isPathVariable(thisPath)) {
                String pathVariable = thisPaths.poll();
                String pathValue = otherPaths.poll();

                pathVariable = pathVariable.substring(1, pathVariable.length() - 1);

                pathVariables.put(pathVariable, pathValue);
                continue;
            }

            if (!Objects.equals(WILD_CARD, thisPath)) {
                return false;
            }

            boolean onlyRemainPathIsWildCard = thisPaths.size() == 1;
            if (onlyRemainPathIsWildCard) {
                return true;
            }

            // wildcard is exist in thisPath.
            thisPaths.poll();
            String nextThisPath = thisPaths.peek();
            while (!otherPaths.isEmpty()) {
                String _otherPath = otherPaths.peek();
                if (Objects.equals(nextThisPath, _otherPath)) {
                    break;
                }
                otherPaths.poll();
            }

            boolean existMatchThisPathButEmptyOtherPath = otherPaths.isEmpty();
            if (existMatchThisPathButEmptyOtherPath) {
                return false;
            }

            return doMatch(thisPaths, otherPaths, pathVariables);
        }

        boolean isAllMatchedPaths = thisPaths.isEmpty() && otherPaths.isEmpty();
        return isAllMatchedPaths;
    }

    private boolean isPathVariable(String thisPath) {
        return thisPath.startsWith("{") && thisPath.endsWith("}");
    }

    public static class Result {
        private final Method method;
        private final Map<String, String> pathVariables;

        public Result(Method method, Map<String, String> pathVariables) {
            this.method = method;
            this.pathVariables = Collections.unmodifiableMap(pathVariables);
        }

        public Method getMethod() {
            return method;
        }

        public Map<String, String> getPathVariables() {
            return pathVariables;
        }
    }

}
