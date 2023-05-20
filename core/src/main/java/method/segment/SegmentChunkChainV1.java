package method.segment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SegmentChunkChainV1 implements SegmentChunk {
    private final SegmentChunkChainV1 nextSegmentChunkChainV1;
    private final SegmentChunk segmentChunk;

    private PathUrl nextPathUrl;

    private SegmentChunkChainV1(SegmentChunk segmentChunk, SegmentChunkChainV1 nextSegmentChunkChainV1) {
        Objects.requireNonNull(segmentChunk);
        this.segmentChunk = segmentChunk;
        this.nextSegmentChunkChainV1 = nextSegmentChunkChainV1;
    }

    public static SegmentChunkChainV1 empty() {
        return new SegmentChunkChainV1(new EmptySegmentChunk(), null);
    }

    public static SegmentChunkChainV1 link(SegmentChunkChainV1 segmentChunkChainV1, SegmentChunk segmentChunk) {
        Objects.requireNonNull(segmentChunk);
        Objects.requireNonNull(segmentChunkChainV1);
        return new SegmentChunkChainV1(segmentChunk, segmentChunkChainV1);
    }

    public static SegmentChunkChainV1 link(SegmentChunkChainV1 segmentChunkChainV1, SegmentChunkChainV1 segmentChunkChainV12) {
        return null;
    }

    public static SegmentChunkChainV1 last(SegmentChunk segmentChunk) {
        Objects.requireNonNull(segmentChunk);
        return new SegmentChunkChainV1(segmentChunk, null);
    }

    public PathVariableValue getPathVariable() {
        if (Objects.isNull(nextPathUrl)) {
            return PathVariableValue.empty();
        }

        if (!(segmentChunk instanceof AbstractPathVariableSegmentChunk)) {
            if (Objects.isNull(nextSegmentChunkChainV1)) {
                return PathVariableValue.empty();
            }
            return nextSegmentChunkChainV1.getPathVariable();
        }

        Map<PathUrl, PathVariableValue> matchedPathVariables = ((AbstractPathVariableSegmentChunk) segmentChunk).getMatchedPathVariables();
        PathVariableValue pathVariableValue = matchedPathVariables.getOrDefault(nextPathUrl, PathVariableValue.empty());

        boolean doesNotLeftChain = Objects.isNull(nextSegmentChunkChainV1);
        if (doesNotLeftChain) {
            return pathVariableValue;
        }

        PathVariableValue nextPathVariableValue = nextSegmentChunkChainV1.getPathVariable();

        return pathVariableValue.merge(nextPathVariableValue);
    }

    @Override
    public List<PathUrl> consume(PathUrl pathUrl) {
        Objects.requireNonNull(pathUrl);

        List<PathUrl> nextPathUrls = segmentChunk.consume(pathUrl);

        boolean doesNotPossibleConsume = nextPathUrls.isEmpty();
        if (doesNotPossibleConsume) {
            return Collections.emptyList();
        }

        boolean doesNotLeftChain = Objects.isNull(nextSegmentChunkChainV1);
        if (doesNotLeftChain) {
            Optional<PathUrl> optionalAllConsumedPathUrl = nextPathUrls.stream()
                .filter(PathUrl::isEmtpy)
                .findFirst();

            if (optionalAllConsumedPathUrl.isEmpty()) {
                return Collections.emptyList();
            }

            PathUrl allConsumedPathUrl = optionalAllConsumedPathUrl.get();
            this.nextPathUrl = allConsumedPathUrl;
            return List.of(allConsumedPathUrl);
        }

        for (PathUrl nextPathUrl : nextPathUrls) {
            this.nextPathUrl = nextPathUrl;

            List<PathUrl> nextChunkChainNextPathUrls = nextSegmentChunkChainV1.consume(nextPathUrl);
            boolean successConsume = !nextChunkChainNextPathUrls.isEmpty();
            if (successConsume) {
                return nextChunkChainNextPathUrls;
            }
        }
        return Collections.emptyList();
    }
}
