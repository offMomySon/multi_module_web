package mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mapper.marker.PathVariable;

public class ElementAnnotationDetector {
    private final AnnotatedElement annotatedElement;

    public ElementAnnotationDetector(AnnotatedElement annotatedElement) {
        if (Objects.isNull(annotatedElement)) {
            throw new RuntimeException("annotatedElement is null.");
        }
        this.annotatedElement = annotatedElement;
    }

    public boolean isAnnotated(Class<?> findAnnotationClass) {
        if (Objects.isNull(findAnnotationClass) || !findAnnotationClass.isAnnotation()) {
            return false;
        }

        return annotatedElement.isAnnotated(findAnnotationClass);
    }

    public <T> Optional<T> find(Class<T> findAnnotationClass) {
        if (Objects.isNull(findAnnotationClass) || !findAnnotationClass.isAnnotation()) {
            return Optional.empty();
        }

        return annotatedElement.find(findAnnotationClass);
    }

    public boolean hasSubElement() {
        return annotatedElement.hasSubElement();
    }

    private boolean doesNotHasSubElement() {
        return !hasSubElement();
    }

    public List<AnnotatedElement> findAnnotatedElementOnSubElement(Class<?> findAnnotation) {
        if(Objects.isNull(findAnnotation) || !findAnnotation.isAnnotation()) {
            return Collections.emptyList();
        }

        if (doesNotHasSubElement()) {
            return Collections.emptyList();
        }

        return annotatedElement.findAnnotatedElementOnSubElement(findAnnotation);
    }
}
