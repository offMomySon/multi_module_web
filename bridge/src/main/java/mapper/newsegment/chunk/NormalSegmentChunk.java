package mapper.newsegment.chunk;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import mapper.MatchSegment;
import mapper.newsegment.SegmentProvider;

public class NormalSegmentChunk implements SegmentChunk {
    private static final String EMPTY_SEGMENT = "";
    
    private final Queue<String> segments;

    public NormalSegmentChunk(Queue<String> segments) {
        Objects.requireNonNull(segments);
        this.segments = segments.stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayDeque::new));
    }

    public static NormalSegmentChunk from(List<String> segments) {
        Objects.requireNonNull(segments);
        ArrayDeque<String> newSegments = segments.stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayDeque::new));
        return new NormalSegmentChunk(newSegments);
    }

    public static NormalSegmentChunk from(String... segments) {
        Objects.requireNonNull(segments);
        ArrayDeque<String> newSegments = Arrays.stream(segments).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayDeque::new));
        return new NormalSegmentChunk(newSegments);
    }

    @Override
    public List<MatchResult> match(SegmentProvider provider) {
        if (Objects.isNull(provider)) {
            throw new RuntimeException("provider is null.");
        }

        boolean doesNotSufficientProvideSegment = segments.size() > provider.size();
        if (doesNotSufficientProvideSegment) {
            return Collections.emptyList();
        }

        Queue<String> thisSegments = new ArrayDeque<>(segments);
        SegmentProvider otherProvider = provider.copy();

        Map<String, String> matchSegments = new HashMap<>();
        while (!thisSegments.isEmpty() && !otherProvider.isEmpty()) {
            String segment = thisSegments.poll();
            String otherSegment = otherProvider.poll();

            if (EMPTY_SEGMENT.equals(otherSegment)) {
                return Collections.emptyList();
            }

            boolean doesNotMatch = !Objects.equals(segment, otherSegment);
            if (doesNotMatch) {
                return Collections.emptyList();
            }

            matchSegments.put(segment, otherSegment);
        }

        boolean doesNotFinishMatch = !thisSegments.isEmpty();
        if (doesNotFinishMatch) {
            return Collections.emptyList();
        }

        MatchSegment matchSegment = new MatchSegment(matchSegments);
        return List.of(new MatchResult(matchSegment, otherProvider.copy()));
    }
}
