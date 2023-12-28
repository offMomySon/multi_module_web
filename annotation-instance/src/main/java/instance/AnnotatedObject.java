package instance;

import java.lang.annotation.Annotation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public class AnnotatedObject {
    private final Annotation annotation;
    private final Object object;

    public AnnotatedObject(@NonNull Annotation annotation, @NonNull Object object) {
        this.annotation = annotation;
        this.object = object;
    }
}