package mapper.newsegment;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public class SegmentProvider {
    private static final String PATH_DELIMITER = "/";

    private final Queue<String> segments;

    private SegmentProvider(Queue<String> segments) {
        Objects.requireNonNull(segments);

        this.segments = segments;
    }

    public static SegmentProvider from(String path) {
        Objects.requireNonNull(path);

        path = path.startsWith(PATH_DELIMITER) ? path.substring(1) : path;

        List<String> segments = Arrays.stream(path.split(PATH_DELIMITER)).collect(Collectors.toUnmodifiableList());

        return new SegmentProvider(new ArrayDeque<>(segments));
    }

    public String poll() {
        return segments.poll();
    }

    public boolean isEmpty() {
        return segments.isEmpty();
    }
}
