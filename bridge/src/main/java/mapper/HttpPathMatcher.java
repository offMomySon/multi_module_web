package mapper;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import mapper.segment.SegmentMatcher;
import mapper.segment.SegmentMatcher.MatchResult;
import marker.RequestMethod;
import vo.RequestValues;

public class HttpPathMatcher {
    private static final String SEGMENT_MATCHER_DELIMITER = "/**";

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

        List<MatchResult> matchResults = match(requestUrl);
        if (matchResults.isEmpty()) {
            return Optional.empty();
        }

        RequestValues requestValues = matchResults.stream()
            .filter(MatchResult::isFinish)
            .findFirst()
            .map(MatchResult::getPathVariable)
            .orElseThrow(() -> new RuntimeException("does not exist finish match result"));

        return Optional.of(new MatchedMethod(javaMethod, requestValues));
    }

    private List<MatchResult> match(String requestUrl) {
        Deque<SegmentMatcher> matcherProvider = createMatchProvider(this.url);
        SequentialSegmentsMatcher matcher = new SequentialSegmentsMatcher(matcherProvider);

        requestUrl = Paths.get(requestUrl).normalize().toString();

        return matcher.match(requestUrl);
    }

    private static Deque<SegmentMatcher> createMatchProvider(String url) {
        Deque<SegmentMatcher> matchers = new ArrayDeque<>();

        int lastIndex;
        while ((lastIndex = url.lastIndexOf(SEGMENT_MATCHER_DELIMITER)) != -1) {
            String lastSubString = url.substring(lastIndex);
            SegmentMatcher segmentMatcher = SegmentMatcher.from(lastSubString);
            matchers.push(segmentMatcher);

            url = url.substring(0, lastIndex);
        }

        if (!url.isBlank()) {
            SegmentMatcher segmentMatcher = SegmentMatcher.from(url);
            matchers.push(segmentMatcher);
        }

        return matchers;
    }

    @Getter
    public static class MatchedMethod {
        private final Method javaMethod;
        private final RequestValues pathVariable;

        public MatchedMethod(Method javaMethod, RequestValues pathVariable) {
            this.javaMethod = javaMethod;
            this.pathVariable = pathVariable;
        }
    }
}