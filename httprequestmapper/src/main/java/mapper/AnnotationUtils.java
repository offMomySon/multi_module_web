package mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class AnnotationUtils {

    public static boolean exist(Class<?> clazz, Class<?> annotationClazz){
        return find(clazz, annotationClazz).isPresent();
    }

    public static boolean exist(Method method, Class<?> annotationClazz){
        return find(method, annotationClazz).isPresent();
    }

    public static boolean exist(Field field, Class<?> annotationClazz){
        return find(field, annotationClazz).isPresent();
    }

    public static <T> Optional<T> find(Class<?> clazz, Class<T> annotationClazz) {
        return find(clazz.getAnnotations(), annotationClazz);
    }

    public static <T> Optional<T> find(Method method, Class<T> annotationClazz) {
        return find(method.getAnnotations(), annotationClazz);
    }

    public static <T> Optional<T> find(Field field, Class<T> annotationClazz) {
        return find(field.getAnnotations(), annotationClazz);
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
