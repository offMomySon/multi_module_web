package mapper;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationUtils {
    private static final Set<Class<?>> selfReferenceAnnotations = Set.of(Retention.class, Target.class, Documented.class);

    public static boolean doesNotExistAll(Class<?> clazz, Class<?>... _annotationClazzes) {
        return !existAll(clazz, _annotationClazzes);
    }

    public static boolean existAll(Class<?> clazz, Class<?>... _annotationClazzes) {
        if (Objects.isNull(clazz) || Objects.isNull(_annotationClazzes) || _annotationClazzes.length == 0) {
            throw new RuntimeException("param is invalid.");
        }

        List<Class<?>> annotationClazzes = Arrays.stream(_annotationClazzes)
            .filter(annotationClazz -> !Objects.isNull(annotationClazz))
            .collect(Collectors.toUnmodifiableList());

        if (annotationClazzes.isEmpty()) {
            throw new RuntimeException("annoataionClazzes is empty.");
        }

        return annotationClazzes.stream()
            .allMatch(annotationClazz -> exist(clazz, annotationClazz));
    }

    public static List<Class<?>> peekFieldsType(Class<?> clazz, Class<?> annotationClass){
        return Arrays.stream(clazz.getDeclaredFields())
            .map(Field::getType)
            .filter(typeClass -> AnnotationUtils.exist(typeClass, annotationClass))
            .collect(Collectors.toUnmodifiableList());
    }

    public static List<Method> peekMethods(Class<?> clazz, Class<?>... _annotatedClazz) {
        if (Objects.isNull(clazz) || Objects.isNull(_annotatedClazz) || _annotatedClazz.length == 0) {
            throw new RuntimeException("parma is invalid.");
        }

        List<Class<?>> annotationClazzes = Arrays.stream(_annotatedClazz)
            .filter(annotationClazz -> !Objects.isNull(annotationClazz))
            .collect(Collectors.toUnmodifiableList());

        if (annotationClazzes.isEmpty()) {
            throw new RuntimeException("annoataionClazzes is empty.");
        }

        Method[] declaredMethods = clazz.getDeclaredMethods();

        return Arrays.stream(declaredMethods)
            .filter(method -> annotationClazzes.stream()
                .allMatch(annotationClazz -> exist(method, annotationClazz)))
            .collect(Collectors.toUnmodifiableList());
    }

    public static boolean exist(Class<?> clazz, Class<?> annotationClazz) {
        return find(clazz, annotationClazz).isPresent();
    }

    public static boolean exist(Method method, Class<?> annotationClazz) {
        return find(method, annotationClazz).isPresent();
    }

    public static <T> Optional<T> find(Class<?> clazz, Class<T> annotationClazz) {
        return find(clazz.getDeclaredAnnotations(), annotationClazz);
    }

    public static <T> Optional<T> find(Method method, Class<T> annotationClazz) {
        return find(method.getDeclaredAnnotations(), annotationClazz);
    }

    private static <T> Optional<T> find(Annotation[] annotations, Class<T> findAnnotationClazz) {
        if (Objects.isNull(annotations) || annotations.length == 0 || !findAnnotationClazz.isAnnotation()) {
            return Optional.empty();
        }

        return Arrays.stream(annotations)
            .map(annotation -> find(annotation, findAnnotationClazz))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }

    private static <T> Optional<T> find(Annotation annotation, Class<T> findAnnotationClazz) {
        if (Objects.isNull(annotation) || !findAnnotationClazz.isAnnotation()) {
            return Optional.empty();
        }

        if (selfReferenceAnnotations.contains(annotation.annotationType())) {
            return Optional.empty();
        }

        if (isAnnotationType(annotation, findAnnotationClazz)) {
            return Optional.of((T) annotation);
        }

        return Arrays.stream(annotation.annotationType().getAnnotations())
            .map(subAnnotation -> find(subAnnotation, findAnnotationClazz))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }

    private static boolean isAnnotationType(Annotation annotation, Class<?> annotationClass) {
        if (Objects.isNull(annotation) || Objects.isNull(annotationClass)) {
            return false;
        }
        return annotation.annotationType() == annotationClass;
    }
}
