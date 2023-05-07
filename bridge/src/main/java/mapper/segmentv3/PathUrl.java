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

        String normalizedPath = Path.of(value.toString()).normalize().toString();
        String firstDelimiterDeletedPath = Objects.equals(String.valueOf(normalizedPath.charAt(0)), DELIMITER) ? normalizedPath.substring(1) : normalizedPath;

        this.value = new StringBuilder(firstDelimiterDeletedPath);
        this.beginIndex = beginIndex;
    }

    public static PathUrl from(String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            throw new RuntimeException("path is empty.");
        }

        StringBuilder value = new StringBuilder(path);
        return new PathUrl(value, 0);
    }

    public static PathUrl empty() {
        return new PathUrl(new StringBuilder("/"), 0);
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

    public String toValue() {
        return "/" + value.toString();
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

        boolean doesNotSameBeginIndex = this.beginIndex != otherPathUrl.beginIndex;
        if (doesNotSameBeginIndex) {
            return false;
        }

        boolean doesNotSameUrlLength = this.value.length() != otherPathUrl.value.length();
        if (doesNotSameUrlLength) {
            return false;
        }

        return Objects.equals(this.value.toString(), otherPathUrl.value.toString());
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
