package mapper.segmentv3;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import mapper.segmentv3.pathvariable.AbstractPathVariableSegmentChunk;

public class SegmentChunkChain implements SegmentChunk {
    private final SegmentChunk segmentChunk;
    private final SegmentChunkChain nextSegmentChunkChain;

    private PathUrl nextPathUrl;

    private SegmentChunkChain(SegmentChunk segmentChunk, SegmentChunkChain nextSegmentChunkChain) {
        Objects.requireNonNull(segmentChunk);
        this.segmentChunk = segmentChunk;
        this.nextSegmentChunkChain = nextSegmentChunkChain;
    }

    public static SegmentChunkChain link(SegmentChunk segmentChunk, SegmentChunkChain segmentChunkChain) {
        Objects.requireNonNull(segmentChunk);
        Objects.requireNonNull(segmentChunkChain);
        return new SegmentChunkChain(segmentChunk, segmentChunkChain);
    }

    public static SegmentChunkChain last(SegmentChunk segmentChunk) {
        Objects.requireNonNull(segmentChunk);
        return new SegmentChunkChain(segmentChunk, null);
    }

    public PathVariable getPathVariable() {
        if (Objects.isNull(nextPathUrl)) {
            return PathVariable.empty();
        }

        if (!(segmentChunk instanceof AbstractPathVariableSegmentChunk)) {
            if (Objects.isNull(nextSegmentChunkChain)) {
                return PathVariable.empty();
            }
            return nextSegmentChunkChain.getPathVariable();
        }
        
        Map<PathUrl, PathVariable> matchedPathVariables = ((AbstractPathVariableSegmentChunk) segmentChunk).getMatchedPathVariables();
        PathVariable pathVariable = matchedPathVariables.getOrDefault(nextPathUrl, PathVariable.empty());

        boolean doesNotLeftChain = Objects.isNull(nextSegmentChunkChain);
        if (doesNotLeftChain) {
            return pathVariable;
        }

        PathVariable nextPathVariable = nextSegmentChunkChain.getPathVariable();

        return pathVariable.merge(nextPathVariable);
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
