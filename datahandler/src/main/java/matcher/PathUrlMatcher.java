package matcher;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableCollectChain;
import matcher.segment.PathVariableValue;
import matcher.segment.SegmentChunk;
import matcher.segment.SegmentChunkFactory;
import static matcher.segment.PathVariableCollectChain.ConsumeResult;

public class PathUrlMatcher {
    private final PathVariableCollectChain baseSegmentChunkChain;

    private PathUrlMatcher(PathVariableCollectChain baseSegmentChunkChain) {
        this.baseSegmentChunkChain = baseSegmentChunkChain;
    }

    public static PathUrlMatcher from(SegmentChunkFactory segmentChunkFactory) {
        Objects.requireNonNull(segmentChunkFactory);

        List<SegmentChunk> segmentChunks = segmentChunkFactory.create();
        Collections.reverse(segmentChunks);
        PathVariableCollectChain baseSegmentChunkChain = PathVariableCollectChain.empty();
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
