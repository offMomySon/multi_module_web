package instance;

import annotation.AnnotationPropertyMappers;
import com.main.util.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import static annotation.AnnotationPropertyMapper.AnnotationProperties;
import static instance.ObjectGraph.ReadOnlyObjectGraph;

public class AnnotatedClassObjectRepository {
    private final AnnotationPropertyMappers propertyMappers;
    private final Map<Class<?>, Object> values;

    public AnnotatedClassObjectRepository(AnnotationPropertyMappers propertyMappers, Map<Class<?>, Object> values) {
        if (Objects.isNull(propertyMappers)) {
            propertyMappers = AnnotationPropertyMappers.empty();
        }
        if (Objects.isNull(values)) {
            values = Collections.emptyMap();
        }

        this.propertyMappers = propertyMappers;
        this.values = values.entrySet().stream()
            .filter(entry -> !Objects.isNull(entry.getKey()))
            .filter(entry -> !Objects.isNull(entry.getValue()))
            .filter(entry -> hasDeclaredAnnotation(entry.getKey()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public static AnnotatedClassObjectRepository emtpy() {
        return new AnnotatedClassObjectRepository(AnnotationPropertyMappers.empty(), Collections.emptyMap());
    }

    public static AnnotatedClassObjectRepository from(AnnotationPropertyMappers propertyMappers, ReadOnlyObjectGraph objectGraph) {
        if (Objects.isNull(propertyMappers)) {
            propertyMappers = AnnotationPropertyMappers.empty();
        }
        if (Objects.isNull(objectGraph)) {
            objectGraph = ReadOnlyObjectGraph.empty();
        }

        Map<Class<?>, Object> rawObjectGraph = objectGraph.copyValues();
        return new AnnotatedClassObjectRepository(propertyMappers, rawObjectGraph);
    }

    private static boolean hasDeclaredAnnotation(Class<?> clazz) {
        return clazz.getDeclaredAnnotations().length != 0;
    }

    public <T> List<T> findObjectByClazz(Class<T> findClazz) {
        if (Objects.isNull(findClazz)) {
            return Collections.emptyList();
        }

        return values.entrySet().stream()
            .filter(entry -> findClazz.isAssignableFrom(entry.getKey()))
            .map(entry -> (T) entry.getValue())
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObject> findObjectByAnnotatedClass(Class<?> findAnnotation) {
        if (Objects.isNull(findAnnotation)) {
            return Collections.emptyList();
        }
        if (!findAnnotation.isAnnotation()) {
            return Collections.emptyList();
        }

        return values.entrySet().stream()
            .filter(entry -> AnnotationUtils.exist(entry.getKey(), findAnnotation))
            .map(entry -> createAnnotatedObject(entry.getKey(), entry.getValue(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObject> findObjectByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation) {
        if (Objects.isNull(findClazz) || Objects.isNull(findAnnotation)) {
            return Collections.emptyList();
        }
        if (!findAnnotation.isAnnotation()) {
            return Collections.emptyList();
        }

        return values.entrySet().stream()
            .filter(entry -> findClazz.isAssignableFrom(entry.getKey()))
            .filter(entry -> AnnotationUtils.exist(entry.getKey(), findAnnotation))
            .map(entry -> createAnnotatedObject(entry.getKey(), entry.getValue(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObjectAndProperties> findObjectAndAnnotationPropertiesByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation, List<String> properties) {
        List<AnnotatedObject> annotatedObjects = findObjectByClassAndAnnotatedClass(findClazz, findAnnotation);

        return annotatedObjects.stream()
            .map(annotatedObject -> {
                Object object = annotatedObject.getObject();
                Annotation annotation = annotatedObject.getAnnotation();

                AnnotationProperties annotationProperties = propertyMappers.getPropertyValues(annotation, properties);
                return new AnnotatedObjectAndProperties(object, annotationProperties);
            })
            .collect(Collectors.toUnmodifiableList());
    }

    private static AnnotatedObject createAnnotatedObject(Class<?> clazz, Object object, Class<?> findAnnotation) {
        Annotation annotation = (Annotation) AnnotationUtils.find(clazz, findAnnotation).orElseThrow(() -> new RuntimeException("Does not exist annotation."));
        return new AnnotatedObject(object, annotation);
    }

    @Getter
    public static class AnnotatedObject {
        private final Object object;
        private final Annotation annotation;

        public AnnotatedObject(Object object, Annotation annotation) {
            Objects.requireNonNull(object);
            Objects.requireNonNull(annotation);
            this.object = object;
            this.annotation = annotation;
        }
    }

    @Getter
    public static class AnnotatedObjectAndProperties {
        private final Object object;
        private final AnnotationProperties annotationProperties;

        public AnnotatedObjectAndProperties(Object object, AnnotationProperties annotationProperties) {
            Objects.requireNonNull(object);
            Objects.requireNonNull(annotationProperties);
            this.object = object;
            this.annotationProperties = annotationProperties;
        }
    }
}
