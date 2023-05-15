package mapper.segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import mapper.segment.pathvariable.AbstractPathVariableSegmentChunk;
import mapper.segment.pathvariable.MatchedPathVariable;

@Slf4j
public class WildCardPathVariableSegmentChunk extends AbstractPathVariableSegmentChunk {
    private static final String WILD_CARD = "**";

    private final PathUrl baseUrl;

    public WildCardPathVariableSegmentChunk(PathUrl baseUrl) {
        Objects.requireNonNull(baseUrl);

        String segment = baseUrl.peekSegment();
        boolean doesNotWildCardAtFirst = !WILD_CARD.equals(segment);
        if (doesNotWildCardAtFirst) {
            throw new RuntimeException("first segment does not wild card.");
        }

        List<String> baseSegments = baseUrl.toList();
        boolean doesNotExistPathVariableSegments = baseSegments.stream().noneMatch(PathVariableUtil::isPathVariable);
        if (doesNotExistPathVariableSegments) {
            throw new RuntimeException("does not exist PathVariableSegment");
        }

        this.baseUrl = baseUrl;
    }

    @Override
    public List<MatchedPathVariable> internalConsume(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        PathUrl copiedBaseUrl = baseUrl.copy();
        copiedBaseUrl.popSegment();
        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk(copiedBaseUrl);

        PathUrl copiedRequestUrl = requestUrl.copy();

        List<MatchedPathVariable> matchedPathVariables = new ArrayList<>();
        while (copiedRequestUrl.doesNotEmpty()) {
            boolean doesNotSufficientRequestUrl = copiedBaseUrl.segmentSize() > copiedRequestUrl.segmentSize();
            if (doesNotSufficientRequestUrl) {
                break;
            }

            List<MatchedPathVariable> subChunkMatchedPathVariables = pathVariableSegmentChunk.internalConsume(copiedRequestUrl);

            boolean doesNotMatch = subChunkMatchedPathVariables.isEmpty();
            if (doesNotMatch) {
                copiedRequestUrl.popSegment();
                continue;
            }

            MatchedPathVariable subChunkMatchedPathVariable = subChunkMatchedPathVariables.get(0);
            matchedPathVariables.add(subChunkMatchedPathVariable);

            copiedRequestUrl.popSegment();
        }

        return matchedPathVariables;
    }
}
