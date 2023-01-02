package mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Annotations {
    private final Annotation[] values;

    public Annotations(Annotation[] values) {
        this.values = values;
    }

    public static Annotations from(Method method){
        return new Annotations(method.getDeclaredAnnotations());
    }

    public static Annotations from(Class<?> clazz){
        return new Annotations(clazz.getDeclaredAnnotations());
    }

    public <T> Optional<T> find(Class<T> annotationClazz) {
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
