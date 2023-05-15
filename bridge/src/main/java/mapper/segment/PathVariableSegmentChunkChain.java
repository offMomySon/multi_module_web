package mapper.segment;

import java.util.Optional;

public interface PathVariableSegmentChunkChain {
    Optional<PathVariableValue> consume(PathUrl pathUrl);
}
