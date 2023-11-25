package matcher.segment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import matcher.segment.path.PathUrl;
import matcher.segment.path.PathUtil;
import matcher.segment.path.PathVariableValue;
import static java.util.Objects.isNull;

@Slf4j
public class WildCardPathVariableSegmentChunk extends AbstractPathVariableSegmentChunk {
    private static final String WILD_CARD = "**";

    private final PathUrl baseUrl;

    public WildCardPathVariableSegmentChunk(PathUrl baseUrl) {
        if (isNull(baseUrl)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }

        String segment = baseUrl.peekSegment();
        boolean doesNotWildCardAtFirst = !WILD_CARD.equals(segment);
        if (doesNotWildCardAtFirst) {
            throw new RuntimeException("first segment does not wild card.");
        }

        List<String> baseSegments = baseUrl.toList();
        boolean doesNotExistPathVariableSegments = baseSegments.stream().noneMatch(PathUtil::isPathVariable);
        if (doesNotExistPathVariableSegments) {
            throw new RuntimeException("does not exist PathVariableSegment");
        }

        this.baseUrl = baseUrl;
    }

    @Override
    public LinkedHashMap<PathUrl, PathVariableValue> internalConsume(PathUrl requestUrl) {
        if (isNull(requestUrl)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }

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