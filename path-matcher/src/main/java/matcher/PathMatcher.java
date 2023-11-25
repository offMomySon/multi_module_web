package matcher;

import java.util.List;
import java.util.Optional;
import matcher.segment.SegmentChunk;
import matcher.segment.SegmentChunkChain;
import matcher.segment.SegmentChunkChain.ConsumeResult;
import matcher.segment.factory.SegmentChunkFactory;
import matcher.path.PathUrl;
import matcher.path.PathVariableValue;
import static java.util.Objects.isNull;

public class PathMatcher {
    private final SegmentChunkChain segmentChunkChain;

    public PathMatcher(SegmentChunkChain segmentChunkChain) {
        if (isNull(segmentChunkChain)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }
        this.segmentChunkChain = segmentChunkChain;
    }

    public static PathMatcher of(PathUrl pathUrl) {
        if (isNull(pathUrl)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }
        List<SegmentChunk> segmentChunks = SegmentChunkFactory.create(pathUrl);

        SegmentChunk headChunk = segmentChunks.get(0);
        SegmentChunkChain headChunkChain = new SegmentChunkChain(headChunk, null);
        SegmentChunkChain nextChunkChain = headChunkChain;
        for (int i = 1; i < segmentChunks.size(); i++) {
            headChunk = segmentChunks.get(i);
            nextChunkChain = nextChunkChain.chaining(headChunk);
        }
        nextChunkChain.close();

        return new PathMatcher(headChunkChain);
    }

    public Optional<PathVariableValue> match(PathUrl requestUrl) {
        if (isNull(requestUrl)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }

        ConsumeResult consumeResult = segmentChunkChain.consume(requestUrl);
        if (consumeResult.doesNotAllConsumed()) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = consumeResult.getPathVariableValue();
        return Optional.of(pathVariableValue);
    }
}