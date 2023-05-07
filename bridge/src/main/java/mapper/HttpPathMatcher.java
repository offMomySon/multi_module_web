package mapper;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import mapper.newsegment.SegmentManager;
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

        Optional<RequestValues> matchResult = SegmentManager.doMatch(this.url, requestUrl);
        if (matchResult.isEmpty()) {
            return Optional.empty();
        }

        RequestValues requestValues = matchResult.get();
        return Optional.of(new MatchedMethod(javaMethod, requestValues));
    }

    private List<MatchResult> match(String requestUrl) {
        Deque<SegmentMatcher> matcherProvider = createMatchProvider(this.url);
        SequentialSegmentMatcher matcher = new SequentialSegmentMatcher(matcherProvider);

        requestUrl = Paths.get(requestUrl).normalize().toString();

        return matcher.match(requestUrl);
    }

    private List<MatchResult> matchV2(String requestUrl) {
        Deque<SegmentMatcher> matcherProvider = createMatchProvider(this.url);
        requestUrl = Paths.get(requestUrl).normalize().toString();
        SequentialSegmentMatcherV2 matcher = SequentialSegmentMatcherV2.bootStrap(matcherProvider, requestUrl);

        return matcher.match();
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