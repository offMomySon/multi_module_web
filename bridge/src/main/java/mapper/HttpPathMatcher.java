package mapper;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import mapper.newsegment.SegmentManager;
import marker.RequestMethod;
import vo.RequestValues;

public class HttpPathMatcher {
    private static final String SEGMENT_MATCHER_DELIMITER = "/**";

    private final RequestMethod requestMethod;
    private final String baseUrl;
    private final Method javaMethod;

    public HttpPathMatcher(RequestMethod requestMethod, String baseUrl, Method javaMethod) {
        this.requestMethod = requestMethod;
        this.baseUrl = baseUrl;
        this.javaMethod = javaMethod;
    }

    public Optional<MatchedMethod> matchMethod(RequestMethod requestMethod, String requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return Optional.empty();
        }
        if (this.requestMethod != requestMethod) {
            return Optional.empty();
        }

        Optional<RequestValues> matchResult = SegmentManager.doMatch(this.baseUrl, requestUrl);
        if (matchResult.isEmpty()) {
            return Optional.empty();
        }

        RequestValues requestValues = matchResult.get();
        return Optional.of(new MatchedMethod(javaMethod, requestValues));
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