package matcher.segment;

import java.util.Optional;

public interface SegmentChunkChain {
    Optional<PathVariableValue> consume(PathUrl pathUrl);
}
