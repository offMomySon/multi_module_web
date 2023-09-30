package annotation;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnnotationPropertyMapper {
    private final Class<?> targetAnnotation;
    private final Map<String, Function<Annotation, ?>> propertyFunctions;

    public AnnotationPropertyMapper(Class<?> targetAnnotation, Map<String, Function<Annotation, ?>> propertyFunctions) {
        Objects.requireNonNull(targetAnnotation);
        Objects.requireNonNull(propertyFunctions);
        this.targetAnnotation = targetAnnotation;
        this.propertyFunctions = propertyFunctions;
    }

    public boolean isSupportAnnotation(Annotation annotation){
        if(Objects.isNull(annotation)){
            return false;
        }
        return annotation.getClass() == targetAnnotation;
    }

    public List<String> getProperties() {
        return propertyFunctions.keySet().stream().collect(Collectors.toUnmodifiableList());
    }

    public Optional<Object> getPropertyValue(Annotation annotation, String property) {
        if (Objects.isNull(annotation) || Objects.isNull(property) || property.isBlank()) {
            return Optional.empty();
        }
        if (targetAnnotation != annotation.getClass()) {
            return Optional.empty();
        }
        if (!propertyFunctions.containsKey(property)) {
            return Optional.empty();
        }

        Function<Annotation, ?> annotationFunction = this.propertyFunctions.get(property);
        Object value = annotationFunction.apply(annotation);
        return Optional.of(value);
    }
}
