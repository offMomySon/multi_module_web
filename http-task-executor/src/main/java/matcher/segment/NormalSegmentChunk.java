package matcher.segment;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NormalSegmentChunk implements SegmentChunk {
    private final PathUrl2 baseUrl;

    public NormalSegmentChunk(PathUrl2 baseUrl) {
        Objects.requireNonNull(baseUrl);
        this.baseUrl = baseUrl;
    }

    @Override
    public List<PathUrl2> consume(PathUrl2 requestUrl) {
        Objects.requireNonNull(requestUrl);

        PathUrl2 copiedBaseUrl = baseUrl.copy();
        PathUrl2 copiedRequestUrl = requestUrl.copy();

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
