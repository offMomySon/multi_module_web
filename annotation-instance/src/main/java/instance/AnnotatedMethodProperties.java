package instance;

import java.lang.reflect.Method;
import java.util.Objects;
import lombok.Getter;

@Getter
public class AnnotatedMethodProperties {
    private final Method javaMethod;
    private final AnnotationProperties annotationProperties;

    public AnnotatedMethodProperties(Method javaMethod, AnnotationProperties annotationProperties) {
        Objects.requireNonNull(javaMethod);
        Objects.requireNonNull(annotationProperties);
        this.javaMethod = javaMethod;
        this.annotationProperties = annotationProperties;
    }
}
