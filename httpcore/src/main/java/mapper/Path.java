package mapper;

import java.util.Objects;

public class Path {
    private static final String WILD_CARD_PATTERN = "**";
    private static final String EMPTY_PATTERN = "";
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";

    private final String value;

    public Path(String value) {
        this.value = value;
    }

    public boolean match(Path otherPath) {
        if (Objects.isNull(otherPath)) {
            return false;
        }
        return Objects.equals(this.value, otherPath.value);
    }

    public boolean isEmpty() {
        return PathUtils.matchPattern(EMPTY_PATTERN, this.value);
    }

    public boolean doesNotWildCard() {
        return !wildCard();
    }

    public boolean wildCard() {
        return PathUtils.matchPattern(WILD_CARD_PATTERN, this.value);
    }

    public boolean isPathVariable() {
        return PathUtils.enclosedBy(PATH_VARIABLE_OPENER, PATH_VARIABLE_CLOSER, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(value, path.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
