package instance;

import com.main.util.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

public class ReadOnlyObjectRepository {
    private final Map<Class<?>, Object> values;

    public ReadOnlyObjectRepository(Map<Class<?>, Object> values) {
        if (Objects.isNull(values)) {
            values = Collections.emptyMap();
        }

        this.values = values.entrySet().stream()
            .filter(entry -> !Objects.isNull(entry.getKey()))
            .filter(entry -> !Objects.isNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public static ReadOnlyObjectRepository empty() {
        return new ReadOnlyObjectRepository(new HashMap<>());
    }

    public Object get(Class<?> key) {
        return values.get(key);
    }

    public boolean containsKey(Class<?> key) {
        return values.containsKey(key);
    }

    public ReadOnlyObjectRepository merge(ReadOnlyObjectRepository other) {
        if (Objects.isNull(other)) {
            return this;
        }

        Stream<Map.Entry<Class<?>, Object>> baseValueStream = this.values.entrySet().stream();
        Stream<Map.Entry<Class<?>, Object>> otherValueStream = other.values.entrySet().stream();

        Map<Class<?>, Object> mergedValues = Stream.concat(baseValueStream, otherValueStream)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
        return new ReadOnlyObjectRepository(mergedValues);
    }

    public <T> List<T> findObjectByClazz(Class<T> findClazz) {
        if (Objects.isNull(findClazz)) {
            return Collections.emptyList();
        }

        return values.entrySet().stream()
            .filter(entry -> findClazz.isAssignableFrom(entry.getKey()))
            .map(entry -> (T) entry.getValue())
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObject> findObjectByAnnotatedClass(Class<?> findAnnotation) {
        if (Objects.isNull(findAnnotation)) {
            return Collections.emptyList();
        }
        if (!findAnnotation.isAnnotation()) {
            return Collections.emptyList();
        }

        return values.entrySet().stream()
            .filter(entry -> AnnotationUtils.exist(entry.getKey(), findAnnotation))
            .map(entry -> createAnnotatedObject(entry.getKey(), entry.getValue(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObject> findObjectByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation) {
        if (Objects.isNull(findClazz) || Objects.isNull(findAnnotation)) {
            return Collections.emptyList();
        }
        if (!findAnnotation.isAnnotation()) {
            return Collections.emptyList();
        }

        return values.entrySet().stream()
            .filter(entry -> findClazz.isAssignableFrom(entry.getKey()))
            .filter(entry -> AnnotationUtils.exist(entry.getKey(), findAnnotation))
            .map(entry -> createAnnotatedObject(entry.getKey(), entry.getValue(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    private static AnnotatedObject createAnnotatedObject(Class<?> clazz, Object object, Class<?> findAnnotation) {
        Annotation annotation = (Annotation) AnnotationUtils.find(clazz, findAnnotation).orElseThrow(() -> new RuntimeException("does not exist annotation."));
        return new AnnotatedObject(object, annotation);
    }

    @Getter
    public static class AnnotatedObject {
        private final Object object;
        private final Annotation annotation;

        public AnnotatedObject(Object object, Annotation annotation) {
            Objects.requireNonNull(object);
            Objects.requireNonNull(annotation);
            this.object = object;
            this.annotation = annotation;
        }
    }

    @Override
    public String toString() {
        return "Container{" +
            "values=" + values +
            '}';
    }
}
