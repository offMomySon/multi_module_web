package instance;

import java.util.Objects;
import lombok.Getter;


@Getter
public class AnnotatedObjectAndProperties {
    private final Object object;
    private final AnnotationProperties annotationProperties;

    public AnnotatedObjectAndProperties(Object object, AnnotationProperties annotationProperties) {
        Objects.requireNonNull(object);
        Objects.requireNonNull(annotationProperties);
        this.object = object;
        this.annotationProperties = annotationProperties;
    }

    public static AnnotatedObjectAndProperties emptyProperty(Object object) {
        return new AnnotatedObjectAndProperties(object, AnnotationProperties.empty());
    }
}
