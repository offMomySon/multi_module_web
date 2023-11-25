package matcher.segment;

import java.util.Collections;
import java.util.List;
import matcher.segment.path.PathUrl;
import static java.util.Objects.isNull;

public class EmptySegmentChunk implements SegmentChunk {
    @Override
    public List<PathUrl> consume(PathUrl pathUrl) {
        if (isNull(pathUrl)) {
            throw new RuntimeException("Must parameter not be null.");
        }

        if (pathUrl.isEmpty()) {
            return List.of(pathUrl);
        }
        return Collections.emptyList();
    }
}