package mapper.segment;

import java.util.Objects;

public abstract class Segment {
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";
    private static final String WILD_CARD_PATTERN = "**";

    public abstract boolean match(String other);

    public static Segment create(String value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException("null 을 segment 로 생성할 수는 없습니다.");
        }

        if (value.equals(WILD_CARD_PATTERN)) {
            return new WildCardSegment();
        }

        if (value.startsWith(PATH_VARIABLE_OPENER) && value.endsWith(PATH_VARIABLE_CLOSER)) {
            return new PathVariableSegment(value);
        }

        return new DefaultSegment(value);
    }
}
