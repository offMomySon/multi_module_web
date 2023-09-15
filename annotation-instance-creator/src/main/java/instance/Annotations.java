package instance;

import com.main.util.AnnotationUtils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Annotations {
    private final List<Class<?>> values;

    public Annotations(List<Class<?>> values) {
        Objects.requireNonNull(values);

        this.values = values.stream()
            .filter(Class::isAnnotation)
            .collect(Collectors.toUnmodifiableList());
    }

    public boolean anyAnnotatedFrom(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return false;
        }

        return values.stream()
            .anyMatch(v -> AnnotationUtils.exist(clazz, v));
    }

    public boolean noneAnnotatedFrom(Class<?> clazz) {
        return !anyAnnotatedFrom(clazz);
    }
}
