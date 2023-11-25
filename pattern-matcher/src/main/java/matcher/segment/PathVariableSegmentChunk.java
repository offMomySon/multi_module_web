package matcher.segment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import matcher.segment.path.PathUrl;
import matcher.segment.path.PathUtil;
import matcher.segment.path.PathVariableValue;
import static java.util.Objects.isNull;

public class PathVariableSegmentChunk extends AbstractPathVariableSegmentChunk {
    private final PathUrl baseUrl;

    public PathVariableSegmentChunk(PathUrl baseUrl) {
        if(isNull(baseUrl)){
            throw new RuntimeException("Ensure the parameter is not null.");
        }

        List<String> segments = baseUrl.toList();
        boolean doesNotHasPathUrl = segments.stream().noneMatch(PathUtil::isPathVariable);
        if (doesNotHasPathUrl) {
            throw new RuntimeException("Does not have path variable segment.");
        }

        this.baseUrl = baseUrl;
    }

    @Override
    public LinkedHashMap<PathUrl, PathVariableValue> internalConsume(PathUrl requestUrl) {
        if (isNull(requestUrl)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }

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

            if (PathUtil.isPathVariable(baseSegment)) {
                String key = PathUtil.parsePathVariable(baseSegment);
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