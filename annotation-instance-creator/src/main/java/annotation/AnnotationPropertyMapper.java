package annotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
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
        this.targetAnnotation = targetAnnotation;
        this.propertyFunctions = propertyFunctions;
    }

    public boolean isSupportAnnotation(Class<?> annotationClazz) {
        return annotationClazz == this.targetAnnotation;
    }

    public List<String> getProperties() {
        return propertyFunctions.keySet().stream().collect(Collectors.toUnmodifiableList());
    }

    public Map<String, Object> getPropertyValue(Annotation annotation, List<String> properties) {
        if (Objects.isNull(annotation) || Objects.isNull(properties)) {
            return Collections.emptyMap();
        }
        if (isSupportAnnotation(annotation.getClass())) {
            return Collections.emptyMap();
        }
        properties = properties.stream()
            .filter(Objects::nonNull)
            .filter(String::isBlank)
            .collect(Collectors.toUnmodifiableList());

        Map<String, Object> propertyValues = new HashMap<>();
        for (String property : properties) {
            if (!propertyFunctions.containsKey(property)) {
                propertyValues.put(property, "");
                continue;
            }
            Function<Annotation, ?> annotationFunction = this.propertyFunctions.get(property);
            Object value = annotationFunction.apply(annotation);
            propertyValues.put(property, value);
        }
        return propertyValues;
    }
}
