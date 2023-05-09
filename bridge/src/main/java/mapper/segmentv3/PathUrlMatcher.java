package mapper.segmentv3;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mapper.segmentv3.strategy.SegmentChunkFactory;

public class PathUrlMatcher {
    private final SegmentChunkChain segmentChunkChain;

    private PathUrlMatcher(SegmentChunkChain segmentChunkChain) {
        Objects.requireNonNull(segmentChunkChain);
        this.segmentChunkChain = segmentChunkChain;
    }

    public static PathUrlMatcher from(PathUrl baseUrl) {
        Objects.requireNonNull(baseUrl);

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

        return new PathUrlMatcher(segmentChunkChain);
    }

    public Optional<PathVariableValue> match(PathUrl requestUrl) {
        if (Objects.isNull(requestUrl)) {
            throw new RuntimeException("path url is empty.");
        }

        List<PathUrl> leftPathUrls = segmentChunkChain.consume(requestUrl);

        boolean doesNotPossibleConsume = leftPathUrls.isEmpty();
        if (doesNotPossibleConsume) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = segmentChunkChain.getPathVariable();
        return Optional.of(pathVariableValue);
    }
}
