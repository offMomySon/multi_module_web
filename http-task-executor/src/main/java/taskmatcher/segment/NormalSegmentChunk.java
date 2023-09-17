package taskmatcher.segment;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NormalSegmentChunk implements SegmentChunk {
    private final PathUrl baseUrl;

    public NormalSegmentChunk(PathUrl baseUrl) {
        Objects.requireNonNull(baseUrl);
        this.baseUrl = baseUrl;
    }

    @Override
    public List<PathUrl> consume(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        PathUrl copiedBaseUrl = baseUrl.copy();
        PathUrl copiedRequestUrl = requestUrl.copy();

        while (copiedBaseUrl.doesNotEmpty()) {
            boolean doesNotSufficientRequestUrl = copiedRequestUrl.isEmtpy();
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
