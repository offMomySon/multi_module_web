package mapper;

import java.util.List;
import java.util.Optional;

public abstract class AnnotatedElement {
    public abstract boolean isAnnotated(Class<?> findAnnotation);

    public abstract <T> Optional<T> find(Class<T> findAnnotation);

    public abstract boolean hasSubElement();

    public abstract List<AnnotatedElement> findAnnotatedElementOnSubElement(Class<?> findAnnotation);
}
