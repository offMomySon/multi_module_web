package mapper.segment;

import java.util.Collections;
import java.util.List;

public class EmptySegmentChunk implements SegmentChunk {
    @Override
    public List<PathUrl> consume(PathUrl pathUrl) {
        if (pathUrl.isEmtpy()) {
            return List.of(pathUrl);
        }
        return Collections.emptyList();
    }
}
