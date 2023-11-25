package matcher.segment.path;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static java.util.Objects.isNull;

public class PathUrl {
    private static final String DELIMITER = "/";

    private final StringBuilder value;
    private int beginIndex;

    public PathUrl(StringBuilder value, int beginIndex) {
        if (isNull(value)) {
            throw new RuntimeException("Must parameter not be null.");
        }
        this.value = value;
        this.beginIndex = beginIndex;
    }

    public static PathUrl from(Path path) {
        return from(path.toString());
    }

    public static PathUrl from(String path) {
        if (isNull(path)) {
            throw new RuntimeException("Must parameter not be null.");
        }

        path = path.trim();
        path = Path.of(path).normalize().toString();
        path = path.startsWith(DELIMITER) ? path.substring(1) : path;

        StringBuilder value = new StringBuilder(path);
        return new PathUrl(value, 0);
    }

    public static PathUrl empty() {
        return PathUrl.from("");
    }

    public boolean isEmpty() {
        return beginIndex >= value.length();
    }

    public boolean doesNotEmpty() {
        return !isEmpty();
    }

    public int segmentSize() {
        if (isEmpty()) {
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
        if (isEmpty()) {
            throw new RuntimeException("Does not left segment.");
        }

        int foundIndex = value.indexOf(DELIMITER, beginIndex);
        boolean lastSegment = foundIndex == -1;
        return lastSegment ?
            value.substring(beginIndex, value.length())
            : value.substring(beginIndex, foundIndex);
    }

    public String popSegment() {
        if (isEmpty()) {
            throw new RuntimeException("Does not left segment.");
        }

        int foundIndex = value.indexOf(DELIMITER, beginIndex);
        boolean lastSegment = foundIndex == -1;
        String popSegment = lastSegment ?
            value.substring(beginIndex, value.length())
            : value.substring(beginIndex, foundIndex);
        beginIndex = lastSegment ? value.length() : foundIndex + 1;

        return popSegment;
    }

    public PathUrl copy() {
        return new PathUrl(this.value, this.beginIndex);
    }

    public String toAbsolutePath() {
        return DELIMITER + this.value;
    }

    public List<String> toList() {
        String remainPath = value.substring(beginIndex);
        return Arrays.stream(remainPath.split(DELIMITER))
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