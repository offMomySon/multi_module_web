package mapper.segment;

import java.util.Objects;

public class WildCardSegment extends Segment {

    @Override
    public boolean match(String other) {
        return Objects.nonNull(other);
    }

    @Override
    public boolean doesNotMatch(String other) {
        return !match(other);
    }
}
