package mapper;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotatedClass {
    private final Class<?> clazz;

    public AnnotatedClass(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            throw new RuntimeException("class is null.");
        }

        this.clazz = clazz;
    }

    public boolean isAnnotated(Class<?> findAnnotation) {
        if (Objects.isNull(findAnnotation) || !findAnnotation.isAnnotation()) {
            return false;
        }

        log.info("anns : {}", Arrays.toString(clazz.getDeclaredAnnotations()));

        return Arrays.stream(clazz.getDeclaredAnnotations())
            .anyMatch(annotation -> isAnnotationType(annotation, findAnnotation));
    }

    public <T> Optional<T> find(Class<T> findAnnotation) {
        if (Objects.isNull(findAnnotation) || !findAnnotation.isAnnotation()) {
            return Optional.empty();
        }

        return Arrays.stream(clazz.getDeclaredAnnotations())
            .filter(annotation -> isAnnotationType(annotation, findAnnotation))
            .map(annotation -> (T)annotation.annotationType())
            .findAny();
    }

    private static boolean isAnnotationType(Annotation annotation, Class<?> annotationClass) {
        if (Objects.isNull(annotation) || Objects.isNull(annotationClass)) {
            return false;
        }
        return annotation.annotationType() == annotationClass;
    }
}
