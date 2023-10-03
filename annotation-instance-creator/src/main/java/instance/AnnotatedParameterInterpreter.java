package instance;

import annotation.AnnotationPropertyMappers;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.main.util.AnnotationUtils.find;

public class AnnotatedParameterInterpreter {
    private final AnnotationPropertyMappers propertyMappers;

    public AnnotatedParameterInterpreter(AnnotationPropertyMappers propertyMappers) {
        Objects.requireNonNull(propertyMappers);
        this.propertyMappers = propertyMappers;
    }

    public AnnotatedParameterProperties interpretProperties(Parameter parameter, Class<?> findAnnotation, List<String> _findProperties) {
        if (Objects.isNull(parameter) || Objects.isNull(findAnnotation) || Objects.isNull(_findProperties)) {
            throw new RuntimeException("Empty parameter.");
        }
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }
        List<String> findProperties = _findProperties.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        if (findProperties.isEmpty()) {
            throw new RuntimeException("Empty parameter.");
        }

        Annotation annotation = (Annotation) find(parameter, findAnnotation).orElseThrow(() -> new RuntimeException("Does not exist find annotation."));
        AnnotationProperties propertyValues = propertyMappers.getPropertyValues(annotation, _findProperties);
        return new AnnotatedParameterProperties(parameter, propertyValues);
    }
}


