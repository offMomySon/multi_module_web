package mapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import mapper.segmentv3.PathUrl;
import mapper.segmentv3.PathVariableValue;
import mapper.segmentv3.SegmentChunk;
import mapper.segmentv3.SegmentChunkChain;
import mapper.segmentv3.strategy.SegmentChunkFactory;
import marker.RequestMethod;

public class HttpPathMatcher {
    private final RequestMethod requestMethod;
    private final SegmentChunkChain baseSegmentChunkChain;
    private final Method javaMethod;

    private HttpPathMatcher(RequestMethod requestMethod, SegmentChunkChain baseSegmentChunkChain, Method javaMethod) {
        this.requestMethod = requestMethod;
        this.baseSegmentChunkChain = baseSegmentChunkChain;
        this.javaMethod = javaMethod;
    }

    public static HttpPathMatcher from(RequestMethod requestMethod, PathUrl baseUrl, Method javaMethod) {
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(baseUrl);
        Objects.requireNonNull(javaMethod);

        List<SegmentChunk> segmentChunks = SegmentChunkFactory.create(baseUrl);
        if (segmentChunks.isEmpty()) {
            throw new RuntimeException("segmentChunk is empty.");
        }

        SegmentChunk lastSegmentChunk = segmentChunks.get(segmentChunks.size() - 1);
        SegmentChunkChain segmentChunkChain = SegmentChunkChain.last(lastSegmentChunk);
        for (int index = segmentChunks.size() - 2; 0 <= index; index--) {
            SegmentChunk prevSegmentChunk = segmentChunks.get(index);
            segmentChunkChain = SegmentChunkChain.link(prevSegmentChunk, segmentChunkChain);
        }

        return new HttpPathMatcher(requestMethod, segmentChunkChain, javaMethod);
    }

    public Optional<MatchedMethod> matchMethod(RequestMethod requestMethod, PathUrl requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return Optional.empty();
        }
        if (this.requestMethod != requestMethod) {
            return Optional.empty();
        }

        List<PathUrl> leftPathUrl = baseSegmentChunkChain.consume(requestUrl);
        boolean doesNotMatch = leftPathUrl.isEmpty();
        if (doesNotMatch) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = baseSegmentChunkChain.getPathVariable();
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