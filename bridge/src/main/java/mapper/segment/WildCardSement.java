package mapper.segment;

import java.util.Objects;

public class WildCardSement extends Segment {

    @Override
    public boolean match(String other) {
        return Objects.nonNull(other);
    }
}
