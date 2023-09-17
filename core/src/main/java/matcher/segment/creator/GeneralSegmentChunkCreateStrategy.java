package matcher.segment.creator;

import matcher.segment.PathVariableUtil;

import java.util.List;
import java.util.Objects;
import matcher.segment.EmptySegmentChunk;
import matcher.segment.NormalSegmentChunk;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableSegmentChunk;
import matcher.segment.SegmentChunk;

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
