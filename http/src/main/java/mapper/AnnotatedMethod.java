package mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AnnotatedMethod extends AnnotatedElement {
    private final Method method;

    public Method getMethod() {
        return method;
    }

    public AnnotatedMethod(Method method) {
        if (Objects.isNull(method)) {
            throw new RuntimeException("method is null.");
        }
        this.method = method;
    }

    @Override
    public boolean isAnnotated(Class<?> findAnnotation) {
        if (Objects.isNull(findAnnotation) || !findAnnotation.isAnnotation()) {
            return false;
        }

        return Arrays.stream(method.getDeclaredAnnotations())
            .anyMatch(annotation -> isAnnotationType(annotation, findAnnotation));
    }

    @Override
    public <T> Optional<T> find(Class<T> findAnnotation) {
        if (Objects.isNull(findAnnotation) || !findAnnotation.isAnnotation()) {
            return Optional.empty();
        }

        return Arrays.stream(method.getDeclaredAnnotations())
            .filter(annotation -> isAnnotationType(annotation, findAnnotation))
            .map(annotation -> (T) annotation)
            .findAny();
    }

    @Override
    public boolean hasSubElement() {
        return false;
    }

    @Override
    public List<AnnotatedElement> findAnnotatedElementOnSubElement(Class<?> findAnnotation) {
        return Collections.emptyList();
    }

    private static boolean isAnnotationType(Annotation annotation, Class<?> annotationClass) {
        if (Objects.isNull(annotation) || Objects.isNull(annotationClass)) {
            return false;
        }
        return annotation.annotationType() == annotationClass;
    }
}
