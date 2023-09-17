package taskmatcher.segment.creator;

import taskmatcher.segment.PathVariableUtil;

import java.util.List;
import java.util.Objects;
import taskmatcher.segment.EmptySegmentChunk;
import taskmatcher.segment.NormalSegmentChunk;
import taskmatcher.segment.PathUrl;
import taskmatcher.segment.PathVariableSegmentChunk;
import taskmatcher.segment.SegmentChunk;

public class GeneralSegmentChunkCreateStrategy {
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
