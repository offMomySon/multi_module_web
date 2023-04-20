package mapper;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import mapper.segment.SegmentsMatcher;
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

        Optional<RequestValues> matchResult = match(requestUrl);
        if (matchResult.isEmpty()) {
            return Optional.empty();
        }

        RequestValues requestValues = matchResult.get();
        return Optional.of(new MatchedMethod(javaMethod, requestValues));
    }

    private Optional<RequestValues> match(String requestUrl) {
        requestUrl = Paths.get(requestUrl).normalize().toString();
        Deque<SegmentsMatcher> matchers = createSegmentsMatcher(this.url);
        SequencialSegmentsMatcher segmentsMatcher = SequencialSegmentsMatcher.from(matchers, requestUrl);

        return segmentsMatcher.match();
    }

    private static Deque<SegmentsMatcher> createSegmentsMatcher(String thisUrl) {
        Deque<SegmentsMatcher> matchers = new ArrayDeque<>();

        int lastIndex;
        while ((lastIndex = thisUrl.lastIndexOf(SEGMENT_MATCHER_DELIMITER)) != -1) {
            String lastSubString = thisUrl.substring(lastIndex);
            thisUrl = thisUrl.substring(0, lastIndex);

            SegmentsMatcher segmentsMatcher = new SegmentsMatcher(lastSubString);
            matchers.push(segmentsMatcher);
        }

        if (!thisUrl.isBlank()) {
            SegmentsMatcher segmentsMatcher = new SegmentsMatcher(thisUrl);
            matchers.push(segmentsMatcher);
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