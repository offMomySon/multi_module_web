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

    public SegmentProvider(Queue<String> segments) {
        Objects.requireNonNull(segments);
        this.segments = segments;
    }

    public static SegmentProvider from(List<String> segments) {
        Objects.requireNonNull(segments);
        return new SegmentProvider(new ArrayDeque<>(segments));
    }

    public static SegmentProvider empty() {
        return new SegmentProvider(new ArrayDeque<>());
    }

    public static SegmentProvider from(String path) {
        Objects.requireNonNull(path);

        if (PATH_DELIMITER.equals(path)) {
            return new SegmentProvider(new ArrayDeque<>(List.of("")));
        }

        path = path.startsWith(PATH_DELIMITER) ? path.substring(1) : path;

        List<String> segments = Arrays.stream(path.split(PATH_DELIMITER)).collect(Collectors.toUnmodifiableList());

        return new SegmentProvider(new ArrayDeque<>(segments));
    }

    public SegmentProvider copy() {
        return new SegmentProvider(new ArrayDeque<>(segments));
    }

    public String peek() {
        return segments.peek();
    }

    public String poll() {
        return segments.poll();
    }

    public boolean isEmpty() {
        return segments.isEmpty();
    }

    public int size() {
        return segments.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SegmentProvider otherProvider = (SegmentProvider) o;
        int otherSize = otherProvider.segments.size();
        int thisSize = this.segments.size();

        if (otherSize != thisSize) {
            return false;
        }

        for (String thisSegment : this.segments) {
            String otherSegment = otherProvider.segments.poll();

            if (!Objects.equals(thisSegment, otherSegment)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "SegmentProvider{" +
            "segments=" + segments +
            '}';
    }
}
