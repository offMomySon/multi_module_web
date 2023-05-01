package mapper.v2segment;

import java.util.List;
import mapper.newsegment.SegmentProvider;

public interface SegmentChunkV2 {
    List<SegmentProvider> match(SegmentProvider provider);
}
