package method.segment;

import java.util.List;

public interface SegmentChunk {
    List<PathUrl> consume(PathUrl pathUrl);
}
