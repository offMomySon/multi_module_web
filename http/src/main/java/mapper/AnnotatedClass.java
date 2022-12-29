package mapper;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotatedClass extends AnnotatedElement {
    private final Class<?> clazz;

    public AnnotatedClass(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            throw new RuntimeException("class is null.");
        }
        this.clazz = clazz;
    }

    @Override
    public boolean isAnnotated(Class<?> findAnnotation) {
        if (Objects.isNull(findAnnotation) || !findAnnotation.isAnnotation()) {
            return false;
        }

        return Arrays.stream(clazz.getDeclaredAnnotations())
            .anyMatch(annotation -> isAnnotationType(annotation, findAnnotation));
    }

    @Override
    public <T> Optional<T> find(Class<T> findAnnotation) {
        if (Objects.isNull(findAnnotation) || !findAnnotation.isAnnotation()) {
            return Optional.empty();
        }

        return Arrays.stream(clazz.getDeclaredAnnotations())
            .filter(annotation -> isAnnotationType(annotation, findAnnotation))
            .map(annotation -> (T) annotation)
            .findAny();
    }

    @Override
    public boolean hasSubElement() {
        if (clazz.getDeclaredMethods().length == 0) {
            return false;
        }
        return true;
    }

    public List<AnnotatedElement> findAnnotatedElementOnSubElement(Class<?> findAnnotation) {
        List<AnnotatedMethod> annotatedMethods = Arrays.stream(clazz.getDeclaredMethods())
            .map(AnnotatedMethod::new)
            .collect(Collectors.toUnmodifiableList());

        return annotatedMethods.stream()
            .filter(annotatedMethod -> annotatedMethod.isAnnotated(findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    private static boolean isAnnotationType(Annotation annotation, Class<?> annotationClass) {
        if (Objects.isNull(annotation) || Objects.isNull(annotationClass)) {
            return false;
        }
        return annotation.annotationType() == annotationClass;
    }
}
