package method.segment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import method.support.PathVariableUtil;

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
    public LinkedHashMap<PathUrl, PathVariableValue> internalConsume(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        PathUrl copiedBaseUrl = baseUrl.copy();
        copiedBaseUrl.popSegment();
        PathUrl copiedRequestUrl = requestUrl.copy();

        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk(copiedBaseUrl.copy());

        LinkedHashMap<PathUrl, PathVariableValue> matchedPathVariableValue = new LinkedHashMap<>();
        while (copiedRequestUrl.doesNotEmpty()) {
            boolean doesNotSufficientRequestUrl = copiedBaseUrl.segmentSize() > copiedRequestUrl.segmentSize();
            if (doesNotSufficientRequestUrl) {
                break;
            }

            Map<PathUrl, PathVariableValue> pathVariableValueMap = pathVariableSegmentChunk.internalConsume(copiedRequestUrl);

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
