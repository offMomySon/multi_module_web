package mapper.segment.strategy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.segment.EmptySegmentChunk;
import mapper.segment.NormalSegmentChunk;
import mapper.segment.PathUrl;
import mapper.segment.PathVariableSegmentChunk;
import mapper.segment.PathVariableUtil;
import mapper.segment.SegmentChunk;

public class GeneralSegmentChunkCreateCreateStrategy {
    public static Deque<SegmentChunk> create(PathUrl basePathUrl) {
        Objects.requireNonNull(basePathUrl);

        if (basePathUrl.isEmtpy()) {
            return Stream.of(new EmptySegmentChunk()).collect(Collectors.toCollection(ArrayDeque::new));
        }

        PathUrl copiedBasePathUrl = basePathUrl.copy();

        boolean hasPathVariable = basePathUrl.toList().stream()
            .anyMatch(PathVariableUtil::isPathVariable);

        if (hasPathVariable) {
            return Stream.of(new PathVariableSegmentChunk(copiedBasePathUrl)).collect(Collectors.toCollection(ArrayDeque::new));
        }
        return Stream.of(new NormalSegmentChunk(copiedBasePathUrl)).collect(Collectors.toCollection(ArrayDeque::new));
    }
}
