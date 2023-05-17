package mapper.segment;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mapper.segment.pathvariable.AbstractPathVariableSegmentChunk;

public class PathVariableCollectChainV2 implements SegmentChunkChain {
    private final PathVariableCollectChainV2 segmentChunkChain;
    private final SegmentChunk segmentChunk;

    public PathVariableCollectChainV2(PathVariableCollectChainV2 segmentChunkChain, SegmentChunk segmentChunk) {
        this.segmentChunkChain = segmentChunkChain;
        this.segmentChunk = segmentChunk;
    }

    public static PathVariableCollectChainV2 empty() {
        return new PathVariableCollectChainV2(null, new EmptySegmentChunk());
    }

    public PathVariableCollectChainV2 chaining(SegmentChunk segmentChunk) {
        Objects.requireNonNull(segmentChunk);
        return new PathVariableCollectChainV2(this, segmentChunk);
    }

    @Override
    public Optional<PathVariableValue> consume(PathUrl requestPathUrl) {
        Objects.requireNonNull(requestPathUrl);

        List<PathUrl> remainPathUrls = segmentChunk.consume(requestPathUrl);

        boolean doesNotMatched = remainPathUrls.isEmpty();
        if (doesNotMatched) {
            return Optional.empty();
        }

        boolean doesNotExistNextChain = Objects.isNull(segmentChunkChain);
        if (doesNotExistNextChain) {
            if (segmentChunk instanceof AbstractPathVariableSegmentChunk) {
                AbstractPathVariableSegmentChunk abstractPathVariableSegmentChunk = (AbstractPathVariableSegmentChunk) segmentChunk;
                PathVariableValue mergedPathVariableValue = remainPathUrls.stream()
                    .reduce(PathVariableValue.empty(), (pv, pu) -> pv.merge(abstractPathVariableSegmentChunk.find(pu)), PathVariableValue::merge);
                return Optional.of(mergedPathVariableValue);
            }
            return Optional.of(PathVariableValue.empty());
        }

        Optional<MatchedPathVariableValue> optionalNextChainMatchPathVariableValue = remainPathUrls.stream()
            .map(this::doConsume)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();

        boolean doesNotMatchNextChain = optionalNextChainMatchPathVariableValue.isEmpty();
        if (doesNotMatchNextChain) {
            return Optional.empty();
        }

        MatchedPathVariableValue matchedPathVariableValue = optionalNextChainMatchPathVariableValue.get();
        PathVariableValue nextChainPathVariableValue = matchedPathVariableValue.getPathVariableValue();
        PathUrl remainPathUrl = matchedPathVariableValue.getRemainPathUrl();

        if (segmentChunk instanceof AbstractPathVariableSegmentChunk) {
            PathVariableValue pathVariableValue = ((AbstractPathVariableSegmentChunk) segmentChunk).find(remainPathUrl);
            nextChainPathVariableValue = pathVariableValue.merge(nextChainPathVariableValue);
        }

        return Optional.of(nextChainPathVariableValue);
    }

    private Optional<MatchedPathVariableValue> doConsume(PathUrl remainPathUrl) {
        Optional<PathVariableValue> optionalPathVariableValue = segmentChunkChain.consume(remainPathUrl);
        if (optionalPathVariableValue.isEmpty()) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = optionalPathVariableValue.get();
        return Optional.of(new MatchedPathVariableValue(remainPathUrl, pathVariableValue));
    }

    private static class MatchedPathVariableValue {
        private final PathUrl remainPathUrl;
        private final PathVariableValue pathVariableValue;

        public MatchedPathVariableValue(PathUrl remainPathUrl, PathVariableValue pathVariableValue) {
            this.remainPathUrl = remainPathUrl;
            this.pathVariableValue = pathVariableValue;
        }

        public PathUrl getRemainPathUrl() {
            return remainPathUrl;
        }

        public PathVariableValue getPathVariableValue() {
            return pathVariableValue;
        }
    }
}
