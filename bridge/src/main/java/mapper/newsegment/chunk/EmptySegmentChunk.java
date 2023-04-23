package mapper.newsegment.chunk;

import java.util.Collections;
import java.util.List;

public class EmptySegmentChunk implements SegmentChunk {
    @Override
    public List<MatchContext> match(MatchContext context) {
        return Collections.emptyList();
    }
}
