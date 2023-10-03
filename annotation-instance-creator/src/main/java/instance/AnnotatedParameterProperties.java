package instance;

import java.lang.reflect.Parameter;
import java.util.Objects;
import lombok.Getter;

@Getter
public class AnnotatedParameterProperties {
    private final Parameter parameter;
    private final AnnotationProperties annotationProperties;

    public AnnotatedParameterProperties(Parameter parameter, AnnotationProperties annotationProperties) {
        Objects.requireNonNull(parameter);
        Objects.requireNonNull(annotationProperties);
        this.parameter = parameter;
        this.annotationProperties = annotationProperties;
    }
}