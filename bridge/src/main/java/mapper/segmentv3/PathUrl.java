package mapper.segmentv3;

import java.nio.file.Path;
import java.util.Objects;

public class PathUrl {
    private static final String DELIMITER = "/";

    private final StringBuilder value;
    private int beginIndex;

    public PathUrl(StringBuilder value, int beginIndex) {
        Objects.requireNonNull(value);
        this.value = new StringBuilder(value);
        this.beginIndex = beginIndex;
    }

    public static PathUrl from(String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            throw new RuntimeException("path is empty.");
        }

        String normalizedPath = Path.of(path).normalize().toString();
        String firstDelimiterDeletedPath = Objects.equals(String.valueOf(normalizedPath.charAt(0)), DELIMITER) ? normalizedPath.substring(1) : normalizedPath;

        StringBuilder value = new StringBuilder(firstDelimiterDeletedPath);

        return new PathUrl(value, 0);
    }

    public boolean isEmtpy() {
        return beginIndex >= value.length();
    }

    public boolean doesNotEmpty() {
        return !isEmtpy();
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
