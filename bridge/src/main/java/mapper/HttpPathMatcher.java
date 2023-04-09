package mapper;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import mapper.segment.PathVariableSegment;
import mapper.segment.Segment;
import mapper.segment.WildCardSegment;
import marker.RequestMethod;
import vo.ParameterValues;

public class HttpPathMatcher {
    private static final String PATH_DELIMITER = "/";
    private static final String EMPTY_PATTERN = "";

    private final RequestMethod requestMethod;
    private final String url;
    private final Method javaMethod;

    public HttpPathMatcher(RequestMethod requestMethod, String url, Method javaMethod) {
        this.requestMethod = requestMethod;
        this.url = url;
        this.javaMethod = javaMethod;
    }

    public Optional<MatchedMethod> matchMethod(RequestMethod requestMethod, String requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return Optional.empty();
        }
        if (this.requestMethod != requestMethod) {
            return Optional.empty();
        }

        ParameterValues pathVariables = ParameterValues.empty();
        if (doesNotMatch(requestUrl, pathVariables)) {
            return Optional.empty();
        }

        return Optional.of(new MatchedMethod(javaMethod, pathVariables));
    }

    private boolean doesNotMatch(String requestUrl, ParameterValues pathVariables) {
        return !match(requestUrl, pathVariables);
    }

    private boolean match(String requestUrl, ParameterValues pathVariables) {
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

        return doMatch(thisPaths, requestPaths, pathVariables);
    }

    private boolean doMatch(List<String> thisPaths, List<String> requestPaths, ParameterValues pathVariables) {
        boolean finishMatch = thisPaths.isEmpty() && requestPaths.isEmpty();
        if (finishMatch) {
            return true;
        }
        boolean onlyRemainThisPath = requestPaths.isEmpty();
        if (onlyRemainThisPath) {
            Segment thisPath = Segment.create(thisPaths.get(0));
            if (!(thisPath instanceof WildCardSegment)) {
                return false;
            }

            boolean onlyRemainWildCard = thisPaths.size() == 1;
            return onlyRemainWildCard;
        }
        boolean onlyRemainRequestPath = thisPaths.isEmpty();
        if (onlyRemainRequestPath) {
            return false;
        }

        Segment thisPath = Segment.create(thisPaths.get(0));
        String requestPath = requestPaths.get(0);

        boolean match = thisPath.match(requestPath);
        if (match) {
            if (thisPath instanceof PathVariableSegment) {
                String key = ((PathVariableSegment) thisPath).getExtractBraceValue();
                pathVariables.put(key, requestPath);

                boolean doMatch = doNextMatch(thisPaths, requestPaths, pathVariables, 1);

                if (!doMatch) {
                    pathVariables.remove(key);
                }

                return doMatch;
            }

            if (thisPath instanceof WildCardSegment) {
                boolean onlyRemainWildCard = thisPaths.size() == 1;
                if (onlyRemainWildCard) {
                    return true;
                }

                List<Integer> nextRequestIndexes = IntStream.range(0, requestPaths.size()).boxed().collect(Collectors.toUnmodifiableList());
                return nextRequestIndexes.stream()
                    .anyMatch(_nextRequestIndex -> doNextMatch(thisPaths, requestPaths, pathVariables, _nextRequestIndex));
            }

            return doNextMatch(thisPaths, requestPaths, pathVariables, 1);
        }
        return false;
    }

    private boolean doNextMatch(List<String> thisPaths, List<String> requestPaths, ParameterValues pathVariables, Integer nextRequestIndex) {
        List<String> nextThisPaths = 1 < thisPaths.size() ?
            thisPaths.subList(1, thisPaths.size()).stream().filter(s -> !Objects.isNull(s)).collect(Collectors.toUnmodifiableList()) :
            Collections.emptyList();
        List<String> nextRequestPaths = nextRequestIndex < requestPaths.size() ?
            requestPaths.subList(nextRequestIndex, requestPaths.size()).stream().filter(s -> !Objects.isNull(s)).collect(Collectors.toUnmodifiableList()) :
            Collections.emptyList();
        return doMatch(nextThisPaths, nextRequestPaths, pathVariables);
    }

    @Getter
    public static class MatchedMethod {
        private final Method javaMethod;
        private final ParameterValues pathVariable;

        public MatchedMethod(Method javaMethod, ParameterValues pathVariable) {
            this.javaMethod = javaMethod;
            this.pathVariable = pathVariable;
        }
    }
}