package mapper.newsegment.chunk;

import java.util.Collections;
import java.util.List;
import mapper.newsegment.SegmentProvider;

public class NormalSegmentChunk implements SegmentChunk {
    @Override
    public List<Result> consume(SegmentProvider segmentProvider) {
        return Collections.emptyList();
    }
}
