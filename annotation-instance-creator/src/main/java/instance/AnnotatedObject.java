package instance;

import java.lang.annotation.Annotation;
import java.util.Objects;
import lombok.Getter;

@Getter
public class AnnotatedObject {
    private final Object object;
    private final Annotation annotation;

    public AnnotatedObject(Object object, Annotation annotation) {
        Objects.requireNonNull(object);
        Objects.requireNonNull(annotation);
        this.object = object;
        this.annotation = annotation;
    }
}