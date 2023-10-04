package instance;

import java.util.Objects;
import lombok.Getter;

@Getter
public class AnnotatedClassProperties {
    private final Class<?> clazz;
    private final AnnotationProperties annotationProperties;

    public AnnotatedClassProperties(Class<?> clazz, AnnotationProperties annotationProperties) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(annotationProperties);
        this.clazz = clazz;
        this.annotationProperties = annotationProperties;
    }

    public static AnnotatedClassProperties emptyProperty(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        return new AnnotatedClassProperties(clazz, AnnotationProperties.empty());
    }
}
