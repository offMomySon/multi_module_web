package mapper.segmentv3;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PathUrl {
    private static final String DELIMITER = "/";

    private final StringBuilder value;
    private int beginIndex;

    public PathUrl(StringBuilder value, int beginIndex) {
        Objects.requireNonNull(value);

        this.value = value;
        this.beginIndex = beginIndex;
    }

    public static PathUrl from(String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            throw new RuntimeException("path is empty.");
        }

        path = path.trim();
        String normalizedPath = Path.of(path).normalize().toString();

        boolean isStartWithDelimiter = Objects.equals(String.valueOf(normalizedPath.charAt(0)), DELIMITER);
        if (isStartWithDelimiter) {
            StringBuilder value = new StringBuilder(normalizedPath);
            return new PathUrl(value, 1);
        }

        StringBuilder value = new StringBuilder(path);
        return new PathUrl(value, 0);
    }

    public static PathUrl empty() {
        return PathUrl.from("/");
    }

    public boolean isEmtpy() {
        return beginIndex >= value.length();
    }

    public boolean doesNotEmpty() {
        return !isEmtpy();
    }

    public int size() {
        if (isEmtpy()) {
            return 0;
        }

        int size = 0;
        int beginIndex = this.beginIndex;

        while (true) {
            int foundIndex = value.indexOf(DELIMITER, beginIndex);

            boolean lastSegment = foundIndex == -1;
            if (lastSegment) {
                size++;
                break;
            }

            size++;
            beginIndex = foundIndex + 1;
        }

        return size;
    }

    public String peekSegment() {
        if (isEmtpy()) {
            throw new RuntimeException("does not left segment.");
        }

        int foundIndex = value.indexOf(DELIMITER, beginIndex);

        boolean lastSegment = foundIndex == -1;
        if (lastSegment) {
            return value.subSequence(beginIndex, value.length()).toString();
        }

        return value.subSequence(beginIndex, foundIndex).toString();
    }

    public String popSegment() {
        if (isEmtpy()) {
            throw new RuntimeException("does not left segment.");
        }

        int foundIndex = value.indexOf(DELIMITER, beginIndex);

        boolean lastSegment = foundIndex == -1;
        if (lastSegment) {
            String segment = value.subSequence(beginIndex, value.length()).toString();
            beginIndex = value.length();

            return segment;
        }

        String segment = value.subSequence(beginIndex, foundIndex).toString();
        beginIndex = foundIndex + 1;

        return segment;
    }

    public PathUrl copy() {
        return new PathUrl(this.value, this.beginIndex);
    }

    public String toAbsolutePath() {
        String value = this.value.toString();

        boolean isStartWithDelimiter = value.startsWith(DELIMITER);
        if (isStartWithDelimiter) {
            return value;
        }

        return "/" + value;
    }

    public List<String> toList() {
        String leftPath = value.substring(beginIndex);

        return Arrays.stream(leftPath.split(DELIMITER))
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathUrl otherPathUrl = (PathUrl) o;

        String thisSubString = this.value.substring(beginIndex);
        String otherSubString = otherPathUrl.value.substring(otherPathUrl.beginIndex);

        return Objects.equals(thisSubString, otherSubString);
    }

    @Override
    public int hashCode() {
        String thisValue = value.toString();
        return Objects.hash(thisValue, beginIndex);
    }

    @Override
    public String toString() {
        return "PathUrl{" +
            "value=" + value +
            ", beginIndex=" + beginIndex +
            '}';
    }
}
