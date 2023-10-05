package instance;

import annotation.AnnotationPropertyMapper;
import java.lang.reflect.Method;
import java.util.Objects;
import lombok.Getter;

@Getter
public class AnnotatedMethodAndProperties {
    private final Method javaMethod;
    private final AnnotationProperties annotationProperties;

    public AnnotatedMethodAndProperties(Method javaMethod, AnnotationProperties annotationProperties) {
        Objects.requireNonNull(javaMethod);
        Objects.requireNonNull(annotationProperties);
        this.javaMethod = javaMethod;
        this.annotationProperties = annotationProperties;
    }
}