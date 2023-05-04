package mapper.segmentv3;

import java.util.List;

public interface SegmentChunk {
    List<PathUrl> consume(PathUrl pathUrl);
}
