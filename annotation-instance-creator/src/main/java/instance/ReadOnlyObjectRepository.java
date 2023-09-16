package instance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReadOnlyObjectRepository {
    private final Map<Class<?>, Object> values;

    public ReadOnlyObjectRepository(Map<Class<?>, Object> values) {
        if (Objects.isNull(values)) {
            this.values = Collections.emptyMap();
            return;
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

    @Override
    public String toString() {
        return "Container{" +
            "values=" + values +
            '}';
    }
}
