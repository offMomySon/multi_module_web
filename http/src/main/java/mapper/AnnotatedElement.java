package mapper;

import java.util.Optional;

public abstract class AnnotatedElement {
    public abstract boolean isAnnotated(Class<?> findAnnotation);

    public abstract <T> Optional<T> find(Class<T> findAnnotation);
}
