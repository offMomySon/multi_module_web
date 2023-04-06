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
import mapper.segment.PathVariableSement;
import mapper.segment.Segment;
import mapper.segment.WildCardSement;
import marker.RequestMethod;
import vo.RequestParameters;

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

        RequestParameters pathVariables = RequestParameters.empty();
        if (doesNotMatch(requestUrl, pathVariables)) {
            return Optional.empty();
        }

        return Optional.of(new MatchedMethod(javaMethod, pathVariables));
    }

    private boolean doesNotMatch(String requestUrl, RequestParameters pathVariables) {
        return !match(requestUrl, pathVariables);
    }

    private boolean match(String requestUrl, RequestParameters pathVariables) {
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

    private boolean doMatch(List<String> thisPaths, List<String> requestPaths, RequestParameters pathVariables) {
        boolean finishMatch = thisPaths.isEmpty() && requestPaths.isEmpty();
        if (finishMatch) {
            return true;
        }
        boolean onlyRemainThisPath = requestPaths.isEmpty();
        if (onlyRemainThisPath) {
            Segment thisPath = Segment.create(thisPaths.get(0));
            if (!(thisPath instanceof WildCardSement)) {
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
            if (thisPath instanceof PathVariableSement) {
                String key = ((PathVariableSement) thisPath).getExtractBraceValue();
                pathVariables.put(key, requestPath);

                List<String> nextThisPaths = 1 < thisPaths.size() ?
                    thisPaths.subList(1, thisPaths.size()).stream().filter(s -> !Objects.isNull(s)).collect(Collectors.toUnmodifiableList()) :
                    Collections.emptyList();
                List<String> nextRequestPaths = 1 < requestPaths.size() ?
                    requestPaths.subList(1, requestPaths.size()).stream().filter(s -> !Objects.isNull(s)).collect(Collectors.toUnmodifiableList()) :
                    Collections.emptyList();
                boolean doMatch = doMatch(nextThisPaths, nextRequestPaths, pathVariables);

                if (!doMatch) {
                    pathVariables.remove(key);
                }

                return doMatch;
            }

            if (thisPath instanceof WildCardSement) {
                boolean onlyRemainWildCard = thisPaths.size() == 1;
                if (onlyRemainWildCard) {
                    return true;
                }

                List<Integer> nextRequestIndexes = IntStream.range(0, requestPaths.size()).boxed().collect(Collectors.toUnmodifiableList());
                return nextRequestIndexes.stream()
                    .anyMatch(_nextRequestIndex -> {
                        List<String> nextThisPaths = 1 < thisPaths.size() ?
                            thisPaths.subList(1, thisPaths.size()).stream().filter(s -> !Objects.isNull(s)).collect(Collectors.toUnmodifiableList()) :
                            Collections.emptyList();
                        List<String> nextRequestPaths = _nextRequestIndex < requestPaths.size() ?
                            requestPaths.subList(_nextRequestIndex, requestPaths.size()).stream().filter(s -> !Objects.isNull(s)).collect(Collectors.toUnmodifiableList()) :
                            Collections.emptyList();
                        return doMatch(nextThisPaths, nextRequestPaths, pathVariables);
                    });
            }

            List<String> nextThisPaths = 1 < thisPaths.size() ?
                thisPaths.subList(1, thisPaths.size()).stream().filter(s -> !Objects.isNull(s)).collect(Collectors.toUnmodifiableList()) :
                Collections.emptyList();
            List<String> nextRequestPaths = 1 < requestPaths.size() ?
                requestPaths.subList(1, requestPaths.size()).stream().filter(s -> !Objects.isNull(s)).collect(Collectors.toUnmodifiableList()) :
                Collections.emptyList();
            return doMatch(nextThisPaths, nextRequestPaths, pathVariables);
        }
        return false;
    }

    @Getter
    public static class MatchedMethod {
        private final Method javaMethod;
        private final RequestParameters pathVariable;

        public MatchedMethod(Method javaMethod, RequestParameters pathVariable) {
            this.javaMethod = javaMethod;
            this.pathVariable = pathVariable;
        }
    }
}