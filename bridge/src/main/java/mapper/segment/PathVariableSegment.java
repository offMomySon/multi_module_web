package mapper.segment;

import java.util.Objects;

public class PathVariableSegment extends Segment {
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";

    private final String value;

    public PathVariableSegment(String value) {
        if (!value.startsWith(PATH_VARIABLE_OPENER)) {
            throw new RuntimeException("valeu 가 pathvariable 형태가 아닙니다.");
        }
        if (!value.endsWith(PATH_VARIABLE_CLOSER)) {
            throw new RuntimeException("valeu 가 pathvariable 형태가 아닙니다.");
        }

        this.value = value;
    }

    public String getExtractBraceValue() {
        return value.substring(
            value.indexOf(PATH_VARIABLE_OPENER) + 1,
            value.indexOf(PATH_VARIABLE_CLOSER)
        );
    }

    @Override
    public boolean match(String other) {
        if (Objects.isNull(other)) {
            return false;
        }
        if (other.isBlank() || other.isEmpty()) {
            return false;
        }

        return true;
    }
}
