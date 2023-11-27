package matcher.segment;

import java.util.Collections;
import java.util.List;

public class EmptySegmentChunk implements SegmentChunk {
    @Override
    public List<PathUrl2> consume(PathUrl2 pathUrl) {
        if (pathUrl.isEmpty()) {
            return List.of(pathUrl);
        }
        return Collections.emptyList();
    }
}
