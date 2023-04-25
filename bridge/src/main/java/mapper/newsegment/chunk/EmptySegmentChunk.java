package mapper.newsegment.chunk;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import mapper.MatchSegment;
import mapper.newsegment.SegmentProvider;

public class EmptySegmentChunk implements SegmentChunk {
    @Override
    public List<MatchResult> match(SegmentProvider segmentProvider) {
        if (Objects.isNull(segmentProvider)) {
            throw new RuntimeException("segmentProvider is null.");
        }

        if (segmentProvider.isEmpty()) {
            return Collections.emptyList();
        }

        SegmentProvider newSegmentProvider = segmentProvider.copy();

        String segment = newSegmentProvider.poll();
        boolean doesNotMatch = !segment.isBlank();
        if (doesNotMatch) {
            return Collections.emptyList();
        }

        MatchSegment emptyMatchSegment = MatchSegment.empty();
        MatchResult matchResult = new MatchResult(emptyMatchSegment, newSegmentProvider);
        return List.of(matchResult);
    }
}
