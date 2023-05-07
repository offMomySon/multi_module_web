package mapper.segmentv3;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import mapper.segmentv3.pathvariable.AbstractPathVariableSegmentChunk;
import mapper.segmentv3.pathvariable.MatchedPathVariable;
import static mapper.segmentv3.PathVariableUtil.isPathVariable;
import static mapper.segmentv3.PathVariableUtil.parsePathVariable;

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
    public List<MatchedPathVariable> internalConsume(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        boolean doesNotSufficientRequestUrl = baseUrl.size() > requestUrl.size();
        if (doesNotSufficientRequestUrl) {
            return Collections.emptyList();
        }

        PathUrl copiedBaseUrl = baseUrl.copy();
        PathUrl copiedRequestUrl = requestUrl.copy();

        PathVariable pathVariable = PathVariable.empty();
        while (copiedBaseUrl.doesNotEmpty()) {
            String baseSegment = copiedBaseUrl.popSegment();
            String requestSegment = copiedRequestUrl.popSegment();

            if (isPathVariable(baseSegment)) {
                String key = parsePathVariable(baseSegment);
                pathVariable.put(key, requestSegment);
                continue;
            }

            boolean doesNotMatch = !Objects.equals(baseSegment, requestSegment);
            if (doesNotMatch) {
                return Collections.emptyList();
            }
        }

        return List.of(new MatchedPathVariable(copiedRequestUrl, pathVariable));
    }
}
