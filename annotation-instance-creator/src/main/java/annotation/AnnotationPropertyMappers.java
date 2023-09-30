package annotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnotationPropertyMappers {
    private final Map<Class<?>, AnnotationPropertyMapper> annotationPropertyMappers2;
    private final List<AnnotationPropertyMapper> annotationPropertyMappers;

    public AnnotationPropertyMappers(Map<Class<?>, AnnotationPropertyMapper> annotationPropertyMappers2) {
        this.annotationPropertyMappers2 = annotationPropertyMappers2;
        this.annotationPropertyMappers = null;
    }

    public AnnotationPropertyMappers(List<AnnotationPropertyMapper> annotationPropertyMappers) {
        Objects.requireNonNull(annotationPropertyMappers);
        this.annotationPropertyMappers = annotationPropertyMappers.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        this.annotationPropertyMappers2 = null;
    }

    public Map<String, Object> getPropertyValue(Annotation annotation, List<String> properties) {
        if (Objects.isNull(annotation) || Objects.isNull(properties)) {
            return Collections.emptyMap();
        }
        Class<? extends Annotation> annotationClass = annotation.getClass();

        if (!annotationPropertyMappers2.containsKey(annotationClass)) {
            return Collections.emptyMap();
        }

        AnnotationPropertyMapper annotationPropertyMapper = annotationPropertyMappers2.get(annotationClass);
        return annotationPropertyMapper.getPropertyValue(annotation, properties);
    }
}