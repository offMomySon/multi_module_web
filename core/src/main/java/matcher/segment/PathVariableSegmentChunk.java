package matcher.segment;

import matcher.creator.PathVariableUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class PathVariableSegmentChunk extends AbstractPathVariableSegmentChunk {
    private final PathUrl baseUrl;

    public PathVariableSegmentChunk(PathUrl baseUrl) {
        Objects.requireNonNull(baseUrl);

        List<String> segments = baseUrl.toList();
        boolean doesNotHasPathUrl = segments.stream().noneMatch(PathVariableUtil::isPathVariable);
        if (doesNotHasPathUrl) {
            throw new RuntimeException("does not have path variable segment.");
        }

        this.baseUrl = baseUrl;
    }

    @Override
    public LinkedHashMap<PathUrl, PathVariableValue> internalConsume(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        boolean doesNotSufficientRequestUrl = baseUrl.segmentSize() > requestUrl.segmentSize();
        if (doesNotSufficientRequestUrl) {
            return new LinkedHashMap<>();
        }

        PathUrl copiedBaseUrl = baseUrl.copy();
        PathUrl copiedRequestUrl = requestUrl.copy();

        PathVariableValue pathVariableValue = PathVariableValue.empty();
        while (copiedBaseUrl.doesNotEmpty()) {
            String baseSegment = copiedBaseUrl.popSegment();
            String requestSegment = copiedRequestUrl.popSegment();

            if (PathVariableUtil.isPathVariable(baseSegment)) {
                String key = PathVariableUtil.parsePathVariable(baseSegment);
                pathVariableValue.put(key, requestSegment);
                continue;
            }

            boolean doesNotMatch = !Objects.equals(baseSegment, requestSegment);
            if (doesNotMatch) {
                return new LinkedHashMap<>();
            }
        }

        LinkedHashMap<PathUrl, PathVariableValue> result = new LinkedHashMap<>();
        result.put(copiedRequestUrl, pathVariableValue);
        return result;
    }
}
