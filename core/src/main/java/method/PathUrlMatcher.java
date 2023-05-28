package method;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import method.segment.PathUrl;
import method.segment.PathVariableCollectChainV2;
import method.segment.PathVariableValue;
import method.segment.SegmentChunk;
import method.segment.SegmentChunkFactory;
import static method.segment.PathVariableCollectChainV2.ConsumeResult;

public class PathUrlMatcher {
    private final PathVariableCollectChainV2 baseSegmentChunkChain;

    private PathUrlMatcher(PathVariableCollectChainV2 baseSegmentChunkChain) {
        this.baseSegmentChunkChain = baseSegmentChunkChain;
    }

    public static PathUrlMatcher from(SegmentChunkFactory segmentChunkFactory) {
        Objects.requireNonNull(segmentChunkFactory);

        // TODO
        // combiner 처리를 고민해봤지만 떠오르지 않는다.
        // merge 느낌이아니라 link 느낌이기 때문에 안되는것 같다.
//        List<SegmentChunk> segmentChunks = segmentChunkFactory.create();
//        Collections.reverse(segmentChunks);
//        PathVariableCollectChainV2 baseSegmentChunkChain = segmentChunks.stream().reduce(PathVariableCollectChainV2.empty(),
//                                                                                         PathVariableCollectChainV2::chaining,
//                                                                                         (sc1, sc2) -> null);
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
