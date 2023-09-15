package instance;

import java.util.Objects;

public class TargetAnnotations {
    private final Class<?> values;

    public TargetAnnotations(Class<?> values) {
        Objects.requireNonNull(values);


        this.values = values;
    }
}
