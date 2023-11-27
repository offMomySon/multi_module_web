package matcher.segment;

import java.util.List;

public interface SegmentChunk {
    List<PathUrl2> consume(PathUrl2 pathUrl);
}
