package mapper.newsegment.chunk;

import java.util.Collections;
import java.util.List;

public class NormalSegmentChunk implements SegmentChunk {
    @Override
    public List<MatchContext> match(MatchContext context) {
        return Collections.emptyList();
    }
}
