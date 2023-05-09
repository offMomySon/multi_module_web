package mapper.segmentv3.strategy;

import java.util.List;
import java.util.Objects;
import mapper.segmentv3.EmptySegmentChunk;
import mapper.segmentv3.NormalSegmentChunk;
import mapper.segmentv3.PathUrl;
import mapper.segmentv3.PathVariableSegmentChunk;
import mapper.segmentv3.PathVariableUtil;
import mapper.segmentv3.SegmentChunk;

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
