package annotation;

import instance.AnnotationProperties;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnnotationPropertyMapper {
    private final Class<?> targetAnnotation;
    private final Map<String, Function<Annotation, ?>> propertyFunctions;

    public AnnotationPropertyMapper(Class<?> targetAnnotation, Map<String, Function<Annotation, ?>> propertyFunctions) {
        Objects.requireNonNull(targetAnnotation);
        Objects.requireNonNull(propertyFunctions);

        if (!targetAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }

        this.targetAnnotation = targetAnnotation;
        this.propertyFunctions = propertyFunctions;
    }

    public boolean isSupportAnnotation(Class<?> annotationClazz) {
        if (Objects.isNull(annotationClazz)) {
            return false;
        }
        return annotationClazz == this.targetAnnotation;
    }

    public boolean doesNotSupportAnnotation(Class<?> annotationClazz) {
        return !isSupportAnnotation(annotationClazz);
    }

    public List<String> getProperties() {
        return propertyFunctions.keySet().stream().collect(Collectors.toUnmodifiableList());
    }

    public AnnotationProperties getPropertyValue(Annotation annotation, List<String> properties) {
        if (Objects.isNull(annotation) || Objects.isNull(properties)) {
            return AnnotationProperties.empty();
        }
        if (isSupportAnnotation(annotation.getClass())) {
            return AnnotationProperties.empty();
        }
        properties = properties.stream()
            .filter(Objects::nonNull)
            .filter(p -> !p.isBlank())
            .collect(Collectors.toUnmodifiableList());

        Map<String, Object> propertyValues = new HashMap<>();
        for (String property : properties) {
            if (!propertyFunctions.containsKey(property)) {
                continue;
            }
            Function<Annotation, ?> annotationFunction = this.propertyFunctions.get(property);
            Object value = annotationFunction.apply(annotation);
            propertyValues.put(property, value);
        }
        return new AnnotationProperties(propertyValues);
    }
}
