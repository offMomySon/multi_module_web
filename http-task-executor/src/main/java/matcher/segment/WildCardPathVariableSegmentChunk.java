package matcher.segment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WildCardPathVariableSegmentChunk extends AbstractPathVariableSegmentChunk {
    private static final String WILD_CARD = "**";

    private final PathUrl2 baseUrl;

    public WildCardPathVariableSegmentChunk(PathUrl2 baseUrl) {
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
    public LinkedHashMap<PathUrl2, PathVariableValue> internalConsume(PathUrl2 requestUrl) {
        Objects.requireNonNull(requestUrl);

        PathUrl2 copiedBaseUrl = baseUrl.copy();
        copiedBaseUrl.popSegment();
        PathUrl2 copiedRequestUrl = requestUrl.copy();

        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk(copiedBaseUrl.copy());

        LinkedHashMap<PathUrl2, PathVariableValue> matchedPathVariableValue = new LinkedHashMap<>();
        while (copiedRequestUrl.doesNotEmpty()) {
            boolean doesNotSufficientRequestUrl = copiedBaseUrl.segmentSize() > copiedRequestUrl.segmentSize();
            if (doesNotSufficientRequestUrl) {
                break;
            }

            Map<PathUrl2, PathVariableValue> pathVariableValueMap = pathVariableSegmentChunk.internalConsume(copiedRequestUrl);

            boolean doesNotMatch = pathVariableValueMap.isEmpty();
            if (doesNotMatch) {
                copiedRequestUrl.popSegment();
                continue;
            }

            matchedPathVariableValue.putAll(pathVariableValueMap);

            copiedRequestUrl.popSegment();
        }

        return matchedPathVariableValue;
    }
}
