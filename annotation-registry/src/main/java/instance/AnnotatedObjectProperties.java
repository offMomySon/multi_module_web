package instance;

import java.util.Objects;
import lombok.Getter;


@Getter
public class AnnotatedObjectProperties {
    private final Object object;
    private final AnnotationProperties annotationProperties;

    public AnnotatedObjectProperties(Object object, AnnotationProperties annotationProperties) {
        Objects.requireNonNull(object);
        Objects.requireNonNull(annotationProperties);
        this.object = object;
        this.annotationProperties = annotationProperties;
    }

    public static AnnotatedObjectProperties emptyProperty(Object object) {
        return new AnnotatedObjectProperties(object, AnnotationProperties.empty());
    }
}
