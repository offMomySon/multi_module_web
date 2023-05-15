package mapper.segment;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import mapper.segment.pathvariable.AbstractPathVariableSegmentChunk;

public class SegmentChunkChainV2 implements PathVariableSegmentChunkChain {
    private final PathVariableSegmentChunkChain segmentChunkChain;
    private final SegmentChunk segmentChunk;

    public SegmentChunkChainV2(PathVariableSegmentChunkChain segmentChunkChain, SegmentChunk segmentChunk) {
        this.segmentChunkChain = segmentChunkChain;
        this.segmentChunk = segmentChunk;
    }

    @Override
    public Optional<PathVariableValue> consume(PathUrl requestPathUrl) {
        Objects.requireNonNull(requestPathUrl);

        List<PathUrl> remainPathUrls = segmentChunk.consume(requestPathUrl);

        boolean doesNotPossibleMatch = requestPathUrl.isEmtpy();
        if (doesNotPossibleMatch) {
            return Optional.empty();
        }

        for (PathUrl remainPathUrl : remainPathUrls) {

            Optional<PathVariableValue> optionalNextChainPathVariableValue = segmentChunkChain.consume(remainPathUrl);

            boolean nextChainMatched = optionalNextChainPathVariableValue.isPresent();
            if (nextChainMatched) {
                
                if (segmentChunk instanceof AbstractPathVariableSegmentChunk) {
                    Map<PathUrl, PathVariableValue> matchedPathVariables = ((AbstractPathVariableSegmentChunk) segmentChunk).getMatchedPathVariables();
                    PathVariableValue pathVariableValue = matchedPathVariables.get(remainPathUrl);
                    PathVariableValue nextChainPathVariableValue = optionalNextChainPathVariableValue.get();
                    PathVariableValue mergePathVariableValue = pathVariableValue.merge(nextChainPathVariableValue);
                    return Optional.of(mergePathVariableValue);
                }

                return optionalNextChainPathVariableValue;
            }
        }

        return Optional.empty();
    }
}
