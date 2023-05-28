package method.segment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PathVariableCollectChainV2 {
    private final PathVariableCollectChainV2 segmentChunkChain;
    private final SegmentChunk segmentChunk;

    public PathVariableCollectChainV2(PathVariableCollectChainV2 segmentChunkChain, SegmentChunk segmentChunk) {
        Objects.requireNonNull(segmentChunk);
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

    public Optional<PathVariableValue> consume(PathUrl requestPathUrl) {
        Objects.requireNonNull(requestPathUrl);

        List<PathUrl> remainPathUrls = segmentChunk.consume(requestPathUrl);

        boolean doesNotMatched = remainPathUrls.isEmpty();
        if (doesNotMatched) {
            return Optional.empty();
        }

        Map<PathUrl, PathVariableValue> matchedPathVariables = getMatchedPathVariables(segmentChunk);

        boolean doesNotExistNextChain = Objects.isNull(segmentChunkChain);
        if (doesNotExistNextChain) {
            Optional<PathUrl> optionalAllConsumedPathUrl = remainPathUrls.stream().filter(PathUrl::isEmtpy).findFirst();

            boolean doesNotExistAllConsumedPathUrl = optionalAllConsumedPathUrl.isEmpty();
            if (doesNotExistAllConsumedPathUrl) {
                return Optional.empty();
            }

            PathUrl allConsumedPathUrl = optionalAllConsumedPathUrl.get();

            PathVariableValue pathVariableValue = matchedPathVariables.getOrDefault(allConsumedPathUrl, PathVariableValue.empty());
            return Optional.of(pathVariableValue);
        }

        Optional<MatchedPathVariableValue> optionalNextChainMatchPathVariableValue = remainPathUrls.stream()
            .map(this::nextConsume)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();

        boolean doesNotExistAllConsumedNextChain = optionalNextChainMatchPathVariableValue.isEmpty();
        if (doesNotExistAllConsumedNextChain) {
            return Optional.empty();
        }

        MatchedPathVariableValue matchedPathVariableValue = optionalNextChainMatchPathVariableValue.get();
        PathVariableValue nextChainPathVariableValue = matchedPathVariableValue.getPathVariableValue();
        PathUrl remainPathUrl = matchedPathVariableValue.getRemainPathUrl();
        
        PathVariableValue pathVariableValue = matchedPathVariables.getOrDefault(remainPathUrl, PathVariableValue.empty());
        pathVariableValue = pathVariableValue.merge(nextChainPathVariableValue);
        return Optional.of(pathVariableValue);
    }

    private static Map<PathUrl, PathVariableValue> getMatchedPathVariables(SegmentChunk segmentChunk) {
        if (segmentChunk instanceof AbstractPathVariableSegmentChunk) {
            AbstractPathVariableSegmentChunk abstractPathVariableSegmentChunk = (AbstractPathVariableSegmentChunk) segmentChunk;
            return abstractPathVariableSegmentChunk.getMatchedPathVariables();
        }
        return new HashMap<>();
    }

    private Optional<MatchedPathVariableValue> nextConsume(PathUrl remainPathUrl) {
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
