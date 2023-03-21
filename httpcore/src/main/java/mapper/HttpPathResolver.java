package mapper;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import vo.HttpMethod;

public class HttpPathResolver {
    private static final String PATH_DELIMITER = "/";
    private static final String WILD_CARD = "**";
    private static final String EMPTY_PATTER = "";

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
            thisPaths = List.of(EMPTY_PATTER);
        } else {
            List<String> splitThisPath = Arrays.stream(this.url.split(PATH_DELIMITER)).collect(Collectors.toUnmodifiableList());
            thisPaths = splitThisPath.subList(1, splitThisPath.size());
        }

        List<String> requestPaths;
        if (Objects.equals(requestUrl, "/")) {
            requestPaths = List.of(EMPTY_PATTER);
        } else {
            List<String> splitRequestPaths = Arrays.stream(requestUrl.split(PATH_DELIMITER)).collect(Collectors.toUnmodifiableList());
            requestPaths = splitRequestPaths.subList(1, splitRequestPaths.size());
        }

        return doMatch(thisPaths, requestPaths, 0, 0);
    }

    private boolean doMatch(List<String> thisPaths, List<String> requestPaths, int thisIndex, int requestIndex) {
        boolean finishMatch = thisPaths.size() <= thisIndex && requestPaths.size() <= requestIndex;
        if (finishMatch) {
            return true;
        }

        boolean onlyRemainThisPaths = requestPaths.size() <= requestIndex;
        if (onlyRemainThisPaths) {
            boolean doesNotWildCard = !Objects.equals(thisPaths.get(thisIndex), WILD_CARD);
            if (doesNotWildCard) {
                return false;
            }

            boolean onlyRemainWildCard = thisPaths.size() <= thisIndex + 1;
            return onlyRemainWildCard;
        }

        boolean onlyRemainRequestPaths = thisPaths.size() <= thisIndex;
        if (onlyRemainRequestPaths) {
            return false;
        }

        String thisPath = thisPaths.get(thisIndex);
        String requestPath = requestPaths.get(requestIndex);
        int nextThisIndex = thisIndex + 1;
        int nextRequestIndex = requestIndex + 1;

        boolean match = Objects.equals(thisPath, requestPath);
        if (match) {
            return doMatch(thisPaths, requestPaths, nextThisIndex, nextRequestIndex);
        }

        boolean pathVariable = thisPath.startsWith("{") && thisPath.endsWith("}");
        if (pathVariable) {
            boolean emptyPatternRequestPath = Objects.equals(EMPTY_PATTER, requestPath);
            if (emptyPatternRequestPath) {
                return false;
            }
            return doMatch(thisPaths, requestPaths, nextThisIndex, nextRequestIndex);
        }

        boolean doesNotWildCard = !Objects.equals(thisPath, WILD_CARD);
        if (doesNotWildCard) {
            return false;
        }

        boolean onlyRemainWildCard = thisPaths.size() <= thisIndex + 1;
        if (onlyRemainWildCard) {
            return true;
        }

        String nextThisPath = thisPaths.get(nextThisIndex);
        boolean nextThisPathPathVariable = nextThisPath.startsWith("{") && nextThisPath.endsWith("}");
        if (nextThisPathPathVariable) {
            List<Integer> doesNotEmptyPatterRequestPathIndexes = IntStream.range(requestIndex, requestPaths.size())
                .boxed()
                .filter(_nextRequestIndex -> {
                    String nextRequestPath = requestPaths.get(_nextRequestIndex);
                    boolean emptyPatternRequestPath = Objects.equals(EMPTY_PATTER, nextRequestPath);
                    return !emptyPatternRequestPath;
                })
                .collect(Collectors.toUnmodifiableList());

            return doesNotEmptyPatterRequestPathIndexes.stream()
                .anyMatch(_nextRequestIndex -> doMatch(thisPaths, requestPaths, nextThisIndex, _nextRequestIndex));
        }

        List<Integer> nextThisPathMatchNextRequestPathIndexes = IntStream.range(requestIndex, requestPaths.size()).boxed()
            .filter(_nextRequestIndex -> {
                String nextRequestPath = requestPaths.get(_nextRequestIndex);
                return Objects.equals(nextThisPath, nextRequestPath);
            })
            .collect(Collectors.toUnmodifiableList());

        return nextThisPathMatchNextRequestPathIndexes.stream()
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