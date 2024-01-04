package instance;

import com.main.util.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Annotations {
    private final List<Class<?>> values;

    public Annotations(@NonNull List<Class<?>> values) {
        this.values = values.stream()
            .filter(Class::isAnnotation)
            .distinct()
            .collect(Collectors.toUnmodifiableList());
    }

    public static Annotations empty() {
        return new Annotations(Collections.emptyList());
    }

    public Annotations merge(Annotations another) {
        if (Objects.isNull(another)) {
            return this;
        }

        List<Class<?>> mergedValues = Stream.concat(this.values.stream(), another.values.stream())
            .distinct()
            .collect(Collectors.toUnmodifiableList());
        return new Annotations(mergedValues);
    }

    public boolean anyAnnotatedFrom(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return false;
        }
        return values.stream().anyMatch(v -> AnnotationUtils.exist(clazz, v));
    }

    public boolean noneAnnotatedFrom(Class<?> clazz) {
        return !anyAnnotatedFrom(clazz);
    }

    public List<Class<?>> peekAnnotatedFieldsFrom(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }

        Field[] declaredFields = clazz.getDeclaredFields();
        return Arrays.stream(declaredFields)
            .map(Field::getType)
            .filter(fieldClazz -> AnnotationUtils.hasAny(fieldClazz, this.values))
            .collect(Collectors.toUnmodifiableList());
    }

    public List<Class<?>> getAnnotations(@NonNull Parameter parameter) {

        Field[] declaredFields = clazz.getDeclaredFields();
        return Arrays.stream(declaredFields)
            .map(Field::getType)
            .filter(fieldClazz -> AnnotationUtils.hasAny(fieldClazz, this.values))
            .collect(Collectors.toUnmodifiableList());
    }

}
