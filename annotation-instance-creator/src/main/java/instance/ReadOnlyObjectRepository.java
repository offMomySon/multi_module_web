package instance;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<Class<?>> foundClazzes = values.keySet().stream()
            .filter(findClazz::isAssignableFrom)
            .collect(Collectors.toUnmodifiableList());

        return foundClazzes.stream()
            .map(foundClazze -> (T) values.get(foundClazze))
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public String toString() {
        return "Container{" +
            "values=" + values +
            '}';
    }
}
