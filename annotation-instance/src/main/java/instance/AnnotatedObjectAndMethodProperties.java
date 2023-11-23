package instance;

import java.util.Objects;
import lombok.Getter;

@Getter
public class AnnotatedObjectAndMethodProperties {
    private final AnnotatedObjectProperties annotatedObjectProperties;
    private final AnnotatedMethodProperties annotatedMethodProperties;

    public AnnotatedObjectAndMethodProperties(AnnotatedObjectProperties annotatedObjectProperties,
                                              AnnotatedMethodProperties annotatedMethodProperties) {
        Objects.requireNonNull(annotatedObjectProperties);
        Objects.requireNonNull(annotatedMethodProperties);
        this.annotatedObjectProperties = annotatedObjectProperties;
        this.annotatedMethodProperties = annotatedMethodProperties;
    }
}