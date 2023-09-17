package taskmatcher;

import taskmatcher.segment.PathUrl;
import taskmatcher.segment.PathVariableCollectChain;
import taskmatcher.segment.PathVariableValue;
import taskmatcher.segment.SegmentChunk;
import taskmatcher.segment.creator.SegmentChunkFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static taskmatcher.segment.PathVariableCollectChain.ConsumeResult;

public class PathUrlMatcher {
    private final PathVariableCollectChain segmentChunkChain;

    private PathUrlMatcher(PathVariableCollectChain segmentChunkChain) {
        this.segmentChunkChain = segmentChunkChain;
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

        ConsumeResult consumeResult = segmentChunkChain.consume(requestUrl);
        if (consumeResult.doesNotAllConsumed()) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = consumeResult.getPathVariableValue();
        return Optional.of(pathVariableValue);
    }
}
