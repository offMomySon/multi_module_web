package mapper.segment.strategy;

import java.util.List;
import java.util.Objects;
import mapper.segment.EmptySegmentChunk;
import mapper.segment.NormalSegmentChunk;
import mapper.segment.PathUrl;
import mapper.segment.PathVariableSegmentChunk;
import mapper.segment.PathVariableUtil;
import mapper.segment.SegmentChunk;

public class GeneralSegmentChunkCreateCreateStrategy {
    public static List<SegmentChunk> create(PathUrl basePathUrl) {
        Objects.requireNonNull(basePathUrl);

        if (basePathUrl.isEmtpy()) {
            return List.of(new EmptySegmentChunk());
        }

        PathUrl copiedBasePathUrl = basePathUrl.copy();

        boolean hasPathVariable = basePathUrl.toList().stream()
            .anyMatch(PathVariableUtil::isPathVariable);

        if (hasPathVariable) {
            return List.of(new PathVariableSegmentChunk(copiedBasePathUrl));
        }
        return List.of(new NormalSegmentChunk(copiedBasePathUrl));
    }
}
