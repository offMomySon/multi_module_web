package mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ClassAnnotationDetector {
    private final Class<?> clazz;

    public ClassAnnotationDetector(Class<?> clazz) {
        this.clazz = clazz;
    }

    public boolean isAnnotatedOnClass(Class<?> annotation) {
        if (Objects.isNull(annotation) || !annotation.isAnnotation()) {
            return false;
        }

        return Arrays.stream(clazz.getDeclaredAnnotations())
            .anyMatch(classAnnotation -> isAnnotationType(classAnnotation, annotation));
    }



    public Set<Method> findAnnotatedMethods(Class<?> annotation) {
        if (Objects.isNull(annotation) || !annotation.isAnnotation()) {
            return Collections.emptySet();
        }

        Set<Method> methods = new HashSet<>();

        for (Method method : clazz.getDeclaredMethods()) {
            for (Annotation findAnnotation : method.getAnnotations()) {
                if (isAnnotationType(findAnnotation, annotation)) {
                    methods.add(method);
                    break;
                }
            }
        }

        return Collections.unmodifiableSet(methods);
    }

    private static boolean isAnnotationType(Annotation annotation, Class<?> annotationClazz) {
        if (Objects.isNull(annotation) || Objects.isNull(annotationClazz)) {
            return false;
        }

        return annotation.annotationType() == annotationClazz;
    }

    public <T> Optional<T> findAnnotationOnClass(Class<T> annotationClass) {
        if (Objects.isNull(annotationClass) || !annotationClass.isAnnotation()) {
            return Optional.empty();
        }

        return Arrays.stream(clazz.getAnnotations())
            .filter(annotation -> isAnnotationType(annotation, annotationClass))
            .map(annotation -> (T)annotation)
            .findFirst();
    }
}
