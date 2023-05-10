package mapper;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import mapper.segment.PathUrl;
import mapper.segment.PathVariableValue;
import marker.RequestMethod;

public class HttpPathMatcher {
    private final RequestMethod requestMethod;
    private final PathUrlMatcher baseUrlPathMatcher;
    private final Method javaMethod;

    public HttpPathMatcher(RequestMethod requestMethod, PathUrlMatcher baseUrlPathMatcher, Method javaMethod) {
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(baseUrlPathMatcher);
        Objects.requireNonNull(javaMethod);
        this.requestMethod = requestMethod;
        this.baseUrlPathMatcher = baseUrlPathMatcher;
        this.javaMethod = javaMethod;
    }

    public static HttpPathMatcher from(RequestMethod requestMethod, String basePathUrl, Method javaMethod) {
        if (Objects.isNull(basePathUrl) || basePathUrl.isBlank()) {
            throw new RuntimeException("basePathUrl is empty.");
        }
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(javaMethod);

        PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(basePathUrl);
        return new HttpPathMatcher(requestMethod, pathUrlMatcher, javaMethod);
    }

    // TODO 변환이 어디까지 허용되는지 모르겠다.
    // 변환. 클래스를 생성하기 위해 어떤 인자에서
//    public static HttpPathMatcher from(RequestMethod requestMethod, PathUrl baseUrl, Method javaMethod) {
//        Objects.requireNonNull(requestMethod);
//        Objects.requireNonNull(baseUrl);
//        Objects.requireNonNull(javaMethod);
//
//        Deque<SegmentChunk> segmentChunks = SegmentChunkFactory.create(baseUrl);
//        if (segmentChunks.isEmpty()) {
//            throw new RuntimeException("segmentChunk is empty.");
//        }
//
//        SegmentChunk lastSegmentChunk = segmentChunks.pop();
//        SegmentChunkChain segmentChunkChain = SegmentChunkChain.last(lastSegmentChunk);
//        while (!segmentChunks.isEmpty()) {
//            SegmentChunk segmentChunk = segmentChunks.pop();
//            segmentChunkChain = SegmentChunkChain.link(segmentChunk, segmentChunkChain);
//        }
//
//        return new HttpPathMatcher(requestMethod, segmentChunkChain, javaMethod);
//    }

    public Optional<MatchedMethod> matchMethod(RequestMethod requestMethod, PathUrl requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return Optional.empty();
        }
        if (this.requestMethod != requestMethod) {
            return Optional.empty();
        }

        Optional<PathVariableValue> optionalPathVariableValue = baseUrlPathMatcher.match(requestUrl);
        boolean doesNotMatch = optionalPathVariableValue.isEmpty();
        if (doesNotMatch) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = optionalPathVariableValue.get();
        return Optional.of(new MatchedMethod(javaMethod, pathVariableValue));
    }

    @Getter
    public static class MatchedMethod {
        private final Method javaMethod;
        private final PathVariableValue pathVariableValue;

        public MatchedMethod(Method javaMethod, PathVariableValue pathVariableValue) {
            Objects.requireNonNull(javaMethod);
            Objects.requireNonNull(pathVariableValue);
            this.javaMethod = javaMethod;
            this.pathVariableValue = pathVariableValue;
        }
    }
}