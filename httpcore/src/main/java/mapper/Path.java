package mapper;

import java.util.Objects;

public class Path {
    private static final String WILD_CARD_PATTERN = "**";
    private static final String EMPTY_PATTERN = "";
    private static final char PATH_VARIABLE_OPENER = '{';
    private static final char PATH_VARIABLE_CLOSER = '}';

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
        return PathUtils.enclosedBy(String.valueOf(PATH_VARIABLE_OPENER), String.valueOf(PATH_VARIABLE_CLOSER), value);
    }

    public String removeBraces() {
        int start = 0;
        int end = value.length()-1;

        if(value.charAt(start) == PATH_VARIABLE_OPENER){
            start++;
        }
        if(value.charAt(end) == PATH_VARIABLE_CLOSER){
            end--;
        }

        return value.substring(start, end+1);
    }

    public String getValue() {
        return value;
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
