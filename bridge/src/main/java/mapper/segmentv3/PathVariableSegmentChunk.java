package mapper.segmentv3;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import static mapper.segmentv3.PathVariableUtil.isPathVariable;
import static mapper.segmentv3.PathVariableUtil.parsePathVariable;

public class PathVariableSegmentChunk implements SegmentChunk {
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";

    private final PathUrl baseUrl;
    private final PathVariable pathVariable = PathVariable.empty();

    public PathVariableSegmentChunk(PathUrl baseUrl) {
        Objects.requireNonNull(baseUrl);
        this.baseUrl = baseUrl;
    }

    @Override
    public List<PathUrl> consume(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        pathVariable.clear();

        boolean doesNotSufficientRequestUrl = baseUrl.size() > requestUrl.size();
        if (doesNotSufficientRequestUrl) {
            return Collections.emptyList();
        }

        PathUrl copiedBaseUrl = baseUrl.copy();
        PathUrl copiedRequestUrl = requestUrl.copy();

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

        return List.of(copiedRequestUrl);
    }

    public PathVariable getPathVariable() {
        return pathVariable.copy();
    }
}
