package mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class AnnotationUtils {

    public static <T> Optional<T> find(Class<?> clazz, Class<T> annotationClazz) {
        return find(clazz.getAnnotations(), annotationClazz);
    }

    public static <T> Optional<T> find(Method method, Class<T> annotationClazz) {
        return find(method.getAnnotations(), annotationClazz);
    }

    private static <T> Optional<T> find(Annotation[] values, Class<T> annotationClazz) {
        if (Objects.isNull(annotationClazz) || !annotationClazz.isAnnotation()) {
            return Optional.empty();
        }

        return Arrays.stream(values)
            .filter(annotation -> isAnnotationType(annotation, annotationClazz))
            .map(annotation -> (T) annotation)
            .findAny();
    }

    private static boolean isAnnotationType(Annotation annotation, Class<?> annotationClass) {
        if (Objects.isNull(annotation) || Objects.isNull(annotationClass)) {
            return false;
        }
        return annotation.annotationType() == annotationClass;
    }
}
