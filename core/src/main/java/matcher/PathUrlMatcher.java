package matcher;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableCollectChainV2;
import matcher.segment.PathVariableValue;
import matcher.segment.SegmentChunk;
import matcher.segment.SegmentChunkFactory;
import static matcher.segment.PathVariableCollectChainV2.ConsumeResult;

public class PathUrlMatcher {
    private final PathVariableCollectChainV2 baseSegmentChunkChain;

    private PathUrlMatcher(PathVariableCollectChainV2 baseSegmentChunkChain) {
        this.baseSegmentChunkChain = baseSegmentChunkChain;
    }

    public static PathUrlMatcher from(SegmentChunkFactory segmentChunkFactory) {
        Objects.requireNonNull(segmentChunkFactory);

        List<SegmentChunk> segmentChunks = segmentChunkFactory.create();
        Collections.reverse(segmentChunks);
        PathVariableCollectChainV2 baseSegmentChunkChain = PathVariableCollectChainV2.empty();
        for (SegmentChunk segmentChunk : segmentChunks) {
            baseSegmentChunkChain = baseSegmentChunkChain.chaining(segmentChunk);
        }
        return new PathUrlMatcher(baseSegmentChunkChain);
    }

    public Optional<PathVariableValue> match(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        ConsumeResult consumeResult = baseSegmentChunkChain.consume(requestUrl);
        if (consumeResult.doesNotAllConsumed()) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = consumeResult.getPathVariableValue();
        return Optional.of(pathVariableValue);
    }
}
