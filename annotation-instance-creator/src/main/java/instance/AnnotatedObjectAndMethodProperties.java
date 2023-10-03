package instance;

import java.util.Objects;
import lombok.Getter;

@Getter
public class AnnotatedObjectAndMethodProperties {
    private final AnnotatedObjectAndProperties annotatedObjectAndProperties;
    private final AnnotatedMethodAndProperties annotatedMethodAndProperties;

    public AnnotatedObjectAndMethodProperties(AnnotatedObjectAndProperties annotatedObjectAndProperties,
                                              AnnotatedMethodAndProperties annotatedMethodAndProperties) {
        Objects.requireNonNull(annotatedObjectAndProperties);
        Objects.requireNonNull(annotatedMethodAndProperties);
        this.annotatedObjectAndProperties = annotatedObjectAndProperties;
        this.annotatedMethodAndProperties = annotatedMethodAndProperties;
    }
}