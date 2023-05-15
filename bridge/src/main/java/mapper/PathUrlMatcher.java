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
    private final SegmentChunkChain baseSegmentChunkChain;

    public PathUrlMatcher(SegmentChunkChain baseSegmentChunkChain) {
        this.baseSegmentChunkChain = baseSegmentChunkChain;
    }

    public static PathUrlMatcher from(PathUrl baseUrl) {
        if (Objects.isNull(baseUrl)) {
            throw new RuntimeException("_baseUrl is empty.");
        }
        List<SegmentChunk> segmentChunks = SegmentChunkFactory.create(baseUrl);

        Collections.reverse(segmentChunks);
        SegmentChunkChain segmentChunkChain = segmentChunks.stream()
            .reduce(SegmentChunkChain.empty(),
                    SegmentChunkChain::link,
                    SegmentChunkChain::link);

        return new PathUrlMatcher(segmentChunkChain);
    }

    public Optional<PathVariableValue> match(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        List<PathUrl> leftPathUrl = baseSegmentChunkChain.consume(requestUrl);
        boolean doesNotMatch = leftPathUrl.isEmpty();
        if (doesNotMatch) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = baseSegmentChunkChain.getPathVariable();
        return Optional.of(pathVariableValue);
    }
}
