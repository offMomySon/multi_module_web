package matcher.segment.factory;

import matcher.segment.EmptySegmentChunk;
import matcher.segment.NormalSegmentChunk;
import matcher.segment.path.PathUrl;
import matcher.segment.path.PathUtil;
import matcher.segment.PathVariableSegmentChunk;
import matcher.segment.SegmentChunk;
import static java.util.Objects.isNull;

public class GeneralSegmentChunkCreateStrategy {
    public static SegmentChunk create(PathUrl basePathUrl) {
        if (isNull(basePathUrl)) {
            throw new RuntimeException("Must parameter not be null.");
        }

        if (basePathUrl.isEmpty()) {
            return new EmptySegmentChunk();
        }

        PathUrl copiedBasePathUrl = basePathUrl.copy();

        boolean hasPathVariable = basePathUrl.toList().stream().anyMatch(PathUtil::isPathVariable);
        if (hasPathVariable) {
            return new PathVariableSegmentChunk(copiedBasePathUrl);
        }
        return new NormalSegmentChunk(copiedBasePathUrl);
    }
}