package instance;

import annotation.AnnotationPropertyMapper.AnnotationProperties;
import annotation.AnnotationPropertyMappers;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import static instance.ReadOnlyObjectRepository.AnnotatedObject;

public class AnnotatedObjectRepository {
    private final ReadOnlyObjectRepository objectRepository;
    private final AnnotationPropertyMappers propertyMappers;

    public AnnotatedObjectRepository(ReadOnlyObjectRepository objectRepository, AnnotationPropertyMappers propertyMappers) {
        Objects.requireNonNull(objectRepository);
        Objects.requireNonNull(propertyMappers);
        this.objectRepository = objectRepository;
        this.propertyMappers = propertyMappers;
    }

    public <T> List<T> findObjectByClazz(Class<T> findClazz) {
        return objectRepository.findObjectByClazz(findClazz);
    }

    public List<AnnotatedObject> findObjectByAnnotatedClass(Class<?> findAnnotation) {
        return objectRepository.findObjectByAnnotatedClass(findAnnotation);
    }

    public List<AnnotatedObject> findObjectByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation) {
        return objectRepository.findObjectByClassAndAnnotatedClass(findClazz, findAnnotation);
    }

    public List<AnnotatedObjectProperties> findObjectAnnotationPropertiesByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation, List<String> properties) {
        List<AnnotatedObject> annotatedObjects = findObjectByClassAndAnnotatedClass(findClazz, findAnnotation);

        return annotatedObjects.stream()
            .map(ao -> {
                Object object = ao.getObject();
                Annotation annotation = ao.getAnnotation();
                AnnotationProperties propertyValue = propertyMappers.getPropertyValue(annotation, properties);
                return new AnnotatedObjectProperties(object, propertyValue);
            })
            .collect(Collectors.toUnmodifiableList());
    }


    @Getter
    public static class AnnotatedObjectProperties {
        private final Object object;
        private final AnnotationProperties annotationProperties;

        public AnnotatedObjectProperties(Object object, AnnotationProperties annotationProperties) {
            Objects.requireNonNull(object);
            Objects.requireNonNull(annotationProperties);
            this.object = object;
            this.annotationProperties = annotationProperties;
        }
    }
}
