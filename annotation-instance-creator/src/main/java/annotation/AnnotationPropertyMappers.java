package annotation;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnnotationPropertyMappers {
    private final List<AnnotationPropertyMapper> annotationPropertyMappers;

    public AnnotationPropertyMappers(List<AnnotationPropertyMapper> annotationPropertyMappers) {
        Objects.requireNonNull(annotationPropertyMappers);
        this.annotationPropertyMappers = annotationPropertyMappers.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
    }

    public boolean isSupportAnnotation(Annotation annotation) {
        if (Objects.isNull(annotation)) {
            return false;
        }
        return annotationPropertyMappers.stream()
            .anyMatch(mapper -> mapper.isSupportAnnotation(annotation));
    }

    public List<String> getProperties(Annotation annotation) {
        return annotationPropertyMappers.stream()
            .filter(mapper -> mapper.isSupportAnnotation(annotation))
            .map(AnnotationPropertyMapper::getProperties)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("does not exist support Annotation."));
    }

    public Optional<Object> getPropertyValue(Annotation annotation, String property) {
        if (Objects.isNull(annotation) || Objects.isNull(property) || property.isBlank()) {
            return Optional.empty();
        }

        return annotationPropertyMappers.stream()
            .filter(mapper -> mapper.isSupportAnnotation(annotation))
            .map(mapper -> mapper.getPropertyValue(annotation, property))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }
}
