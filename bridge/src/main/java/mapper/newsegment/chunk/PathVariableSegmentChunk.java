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

public class PathVariableSegmentChunk implements SegmentChunk {
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";

    private final Queue<String> segments;

    public PathVariableSegmentChunk(Queue<String> segments) {
        Objects.requireNonNull(segments);
        this.segments = segments.stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayDeque::new));
    }

    public static PathVariableSegmentChunk from(List<String> segments) {
        Objects.requireNonNull(segments);
        ArrayDeque<String> newSegments = segments.stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayDeque::new));
        return new PathVariableSegmentChunk(newSegments);
    }

    public static PathVariableSegmentChunk from(String... segments) {
        Objects.requireNonNull(segments);
        ArrayDeque<String> newSegments = Arrays.stream(segments).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayDeque::new));
        return new PathVariableSegmentChunk(newSegments);
    }

    @Override
    public List<Result> consume(SegmentProvider provider) {
        Objects.requireNonNull(provider);

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

            if (isPathVariable(segment)) {
                matchSegments.put(segment, otherSegment);
                continue;
            }

            boolean doesNotMatch = !Objects.equals(segment, otherSegment);
            if (doesNotMatch) {
                return Collections.emptyList();
            }

            matchSegments.put(segment, otherSegment);
        }

        boolean doesNotMatch = !thisSegments.isEmpty();
        if (doesNotMatch) {
            return Collections.emptyList();
        }

        MatchSegment matchSegment = new MatchSegment(matchSegments);
        return List.of(new Result(matchSegment, otherProvider.copy()));
    }

    private static boolean isPathVariable(String segment) {
        return segment.startsWith(PATH_VARIABLE_OPENER) && segment.endsWith(PATH_VARIABLE_CLOSER);
    }
}
