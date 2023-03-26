package mapper.segment;

import java.util.Objects;
import lombok.NonNull;

public class DefaultSegment extends Segment {
    private final String value;

    public DefaultSegment(@NonNull String value) {
        this.value = value;
    }

    @Override
    public boolean match(String other) {
        if (Objects.isNull(other)) {
            return false;
        }

        return Objects.equals(value, other);
    }
}
