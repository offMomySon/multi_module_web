package matcher.segment;

import java.util.List;
import matcher.path.PathUrl;

public interface SegmentChunk {
    List<PathUrl> consume(PathUrl pathUrl);
}