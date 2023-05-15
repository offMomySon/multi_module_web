package mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mapper.segment.PathUrl;
import mapper.segment.PathVariableValue;
import mapper.segment.SegmentChunk;
import mapper.segment.SegmentChunkChain;
import mapper.segment.strategy.SegmentChunkFactory;

public class PathUrlMatcher {
    private final SegmentChunkFactory segmentChunkFactory;

    private PathUrlMatcher(SegmentChunkFactory segmentChunkFactory) {
        Objects.requireNonNull(segmentChunkFactory);
        this.segmentChunkFactory = segmentChunkFactory;
    }

    public static PathUrlMatcher from(PathUrl baseUrl) {
        if (Objects.isNull(baseUrl)) {
            throw new RuntimeException("_baseUrl is empty.");
        }

        SegmentChunkFactory segmentChunkFactory = new SegmentChunkFactory(baseUrl);
        return new PathUrlMatcher(segmentChunkFactory);
    }

    public Optional<PathVariableValue> match(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        List<SegmentChunk> segmentChunks = segmentChunkFactory.create();
        Collections.reverse(segmentChunks);
        SegmentChunkChain baseSegmentChunkChain = segmentChunks.stream()
            .reduce(SegmentChunkChain.empty(), SegmentChunkChain::link, SegmentChunkChain::link);

        List<PathUrl> leftPathUrl = baseSegmentChunkChain.consume(requestUrl);
        boolean doesNotMatch = leftPathUrl.isEmpty();
        if (doesNotMatch) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = baseSegmentChunkChain.getPathVariable();
        return Optional.of(pathVariableValue);
    }
}
