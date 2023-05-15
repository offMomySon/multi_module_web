package mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mapper.segment.PathUrl;
import mapper.segment.PathVariableValue;
import mapper.segment.SegmentChunk;
import mapper.segment.SegmentChunkChain;
import mapper.segment.strategy.SegmentChunkFactory;

public class PathUrlMatcher {
    private final PathUrl baseUrl;

    public PathUrlMatcher(PathUrl baseUrl) {
        Objects.requireNonNull(baseUrl);
        this.baseUrl = baseUrl;
    }

    public static PathUrlMatcher from(String baseUrl) {
        if (Objects.isNull(baseUrl) || baseUrl.isBlank()) {
            throw new RuntimeException("_baseUrl is empty.");
        }
        return new PathUrlMatcher(PathUrl.from(baseUrl));
    }

    public Optional<PathVariableValue> match(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        List<SegmentChunk> baseSegmentChunks = SegmentChunkFactory.create(baseUrl);
        SegmentChunkChain baseSegmentChunkChain = createSegmentChunkChain(baseSegmentChunks);

        List<PathUrl> leftPathUrl = baseSegmentChunkChain.consume(requestUrl);
        boolean doesNotMatch = leftPathUrl.isEmpty();
        if (doesNotMatch) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = baseSegmentChunkChain.getPathVariable();
        return Optional.of(pathVariableValue);
    }

    private static SegmentChunkChain createSegmentChunkChain(List<SegmentChunk> segmentChunks) {
        if (segmentChunks.isEmpty()) {
            throw new RuntimeException("segmentChunk is empty.");
        }

        List<SegmentChunk> copiedSegmentChunks = new ArrayList<>(segmentChunks);
        Collections.reverse(copiedSegmentChunks);

        return copiedSegmentChunks.stream()
            .reduce(SegmentChunkChain.empty(),
                    SegmentChunkChain::link,
                    SegmentChunkChain::link
            );
    }
}
