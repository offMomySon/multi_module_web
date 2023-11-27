package matcher;

import matcher.segment.PathUrl2;
import matcher.segment.PathVariableCollectChain;
import matcher.segment.PathVariableValue;
import matcher.segment.SegmentChunk;
import matcher.segment.factory.SegmentChunkFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static matcher.segment.PathVariableCollectChain.ConsumeResult;

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

    public Optional<PathVariableValue> match(PathUrl2 requestUrl) {
        Objects.requireNonNull(requestUrl);

        ConsumeResult consumeResult = segmentChunkChain.consume(requestUrl);
        if (consumeResult.doesNotAllConsumed()) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = consumeResult.getPathVariableValue();
        return Optional.of(pathVariableValue);
    }
}
