package matcher.segment;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import matcher.segment.path.PathUrl;
import static java.util.Objects.isNull;

public class NormalSegmentChunk implements SegmentChunk {
    private final PathUrl baseUrl;

    public NormalSegmentChunk(PathUrl baseUrl) {
        if (isNull(baseUrl)) {
            throw new RuntimeException("Must parameter not be null.");
        }
        this.baseUrl = baseUrl;
    }

    @Override
    public List<PathUrl> consume(PathUrl requestUrl) {
        if (isNull(requestUrl)) {
            throw new RuntimeException("Must parameter not be null.");
        }

        PathUrl copiedBaseUrl = baseUrl.copy();
        PathUrl copiedRequestUrl = requestUrl.copy();

        while (copiedBaseUrl.doesNotEmpty()) {
            boolean doesNotSufficientRequestUrl = copiedRequestUrl.isEmpty();
            if (doesNotSufficientRequestUrl) {
                return Collections.emptyList();
            }

            String baseSegment = copiedBaseUrl.popSegment();
            String requestSegment = copiedRequestUrl.popSegment();

            boolean doesNotMatch = !Objects.equals(baseSegment, requestSegment);
            if (doesNotMatch) {
                return Collections.emptyList();
            }
        }

        return List.of(copiedRequestUrl.copy());
    }
}