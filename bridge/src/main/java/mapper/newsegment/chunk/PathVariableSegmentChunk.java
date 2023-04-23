package mapper.newsegment.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import mapper.newsegment.SegmentProvider;

public class PathVariableSegmentChunk implements SegmentChunk {

    private final List<String> segments;

    public PathVariableSegmentChunk(String... segments) {
        Objects.requireNonNull(segments);
        this.segments = Arrays.stream(segments).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Result> consume(SegmentProvider segmentProvider) {
        return null;
    }

}
