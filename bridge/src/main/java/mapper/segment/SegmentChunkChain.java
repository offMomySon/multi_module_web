package mapper.segment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import mapper.segment.pathvariable.AbstractPathVariableSegmentChunk;

public class SegmentChunkChain implements SegmentChunk {
    private final SegmentChunkChain nextSegmentChunkChain;
    private final SegmentChunk segmentChunk;

    private PathUrl nextPathUrl;

    private SegmentChunkChain(SegmentChunk segmentChunk, SegmentChunkChain nextSegmentChunkChain) {
        Objects.requireNonNull(segmentChunk);
        this.segmentChunk = segmentChunk;
        this.nextSegmentChunkChain = nextSegmentChunkChain;
    }

    public static SegmentChunkChain empty() {
        return new SegmentChunkChain(new EmptySegmentChunk(), null);
    }

    public static SegmentChunkChain link(SegmentChunkChain segmentChunkChain, SegmentChunk segmentChunk) {
        Objects.requireNonNull(segmentChunk);
        Objects.requireNonNull(segmentChunkChain);
        return new SegmentChunkChain(segmentChunk, segmentChunkChain);
    }

    public static SegmentChunkChain link(SegmentChunkChain segmentChunkChain, SegmentChunkChain segmentChunkChain2) {
        return null;
    }

    public static SegmentChunkChain last(SegmentChunk segmentChunk) {
        Objects.requireNonNull(segmentChunk);
        return new SegmentChunkChain(segmentChunk, null);
    }

    public PathVariableValue getPathVariable() {
        if (Objects.isNull(nextPathUrl)) {
            return PathVariableValue.empty();
        }

        if (!(segmentChunk instanceof AbstractPathVariableSegmentChunk)) {
            if (Objects.isNull(nextSegmentChunkChain)) {
                return PathVariableValue.empty();
            }
            return nextSegmentChunkChain.getPathVariable();
        }

        Map<PathUrl, PathVariableValue> matchedPathVariables = ((AbstractPathVariableSegmentChunk) segmentChunk).getMatchedPathVariables();
        PathVariableValue pathVariableValue = matchedPathVariables.getOrDefault(nextPathUrl, PathVariableValue.empty());

        boolean doesNotLeftChain = Objects.isNull(nextSegmentChunkChain);
        if (doesNotLeftChain) {
            return pathVariableValue;
        }

        PathVariableValue nextPathVariableValue = nextSegmentChunkChain.getPathVariable();

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

        boolean doesNotLeftChain = Objects.isNull(nextSegmentChunkChain);
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

            List<PathUrl> nextChunkChainNextPathUrls = nextSegmentChunkChain.consume(nextPathUrl);
            boolean successConsume = !nextChunkChainNextPathUrls.isEmpty();
            if (successConsume) {
                return nextChunkChainNextPathUrls;
            }
        }
        return Collections.emptyList();
    }
}
