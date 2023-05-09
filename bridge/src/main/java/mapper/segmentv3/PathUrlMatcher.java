package mapper.segmentv3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mapper.segmentv3.strategy.SegmentChunkCreateFactory;

public class PathUrlMatcher {
    private final PathUrl baseUrl;

    public PathUrlMatcher(PathUrl baseUrl) {
        Objects.requireNonNull(baseUrl);
        this.baseUrl = baseUrl;
    }

    public Optional<PathVariable> match(PathUrl requestUrl) {
        if (Objects.isNull(requestUrl)) {
            throw new RuntimeException("path url is empty.");
        }

        List<SegmentChunk> segmentChunks = SegmentChunkCreateFactory.create(this.baseUrl);
        if (segmentChunks.isEmpty()) {
            return Optional.empty();
        }

        List<SegmentChunk> newSegmentChunks = new ArrayList<>();
        for (int i = segmentChunks.size() - 1; 0 <= i; i--) {
            newSegmentChunks.add(segmentChunks.get(i));
        }

        Iterator<SegmentChunk> iterator = newSegmentChunks.iterator();
        SegmentChunk lastSegmentChunk = iterator.next();
        SegmentChunkChain segmentChunkChain = SegmentChunkChain.last(lastSegmentChunk);

        while (iterator.hasNext()) {
            SegmentChunk prevSegmentChunk = iterator.next();
            segmentChunkChain = SegmentChunkChain.link(prevSegmentChunk, segmentChunkChain);
        }

        List<PathUrl> leftPathUrls = segmentChunkChain.consume(requestUrl);

        boolean doesNotPossibleConsume = leftPathUrls.isEmpty();
        if (doesNotPossibleConsume) {
            return Optional.empty();
        }

        PathVariable pathVariable = segmentChunkChain.getPathVariable();
        return Optional.of(pathVariable);
    }
}
