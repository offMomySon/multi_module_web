package annotation;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import static annotation.AnnotationPropertyMapper.AnnotationProperties;

public class AnnotationPropertyMappers {
    private final Map<Class<?>, AnnotationPropertyMapper> targetMappers;

    public AnnotationPropertyMappers(Map<Class<?>, AnnotationPropertyMapper> targetMappers) {
        Objects.requireNonNull(targetMappers);
        this.targetMappers = targetMappers.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public AnnotationProperties getPropertyValues(Annotation annotation, List<String> properties) {
        if (Objects.isNull(annotation) || Objects.isNull(properties)) {
            return AnnotationProperties.empty();
        }
        Class<? extends Annotation> annotationType = annotation.annotationType();

        if (!targetMappers.containsKey(annotationType)) {
            return AnnotationProperties.empty();
        }

        AnnotationPropertyMapper annotationPropertyMapper = targetMappers.get(annotationType);
        return annotationPropertyMapper.getPropertyValue(annotation, properties);
    }
}
