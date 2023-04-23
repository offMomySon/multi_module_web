package mapper.newsegment.chunk;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import mapper.MatchSegment;
import mapper.newsegment.SegmentProvider;

public class EmptySegmentChunk implements SegmentChunk {
    @Override
    public List<Result> consume(SegmentProvider segmentProvider) {
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

        Result result = new Result(emptyMatchSegment, newSegmentProvider);
        return List.of(result);
    }
}
