package instance;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectRepository {
    private final Map<Class<?>, Object> values;

    public ObjectRepository(Map<Class<?>, Object> values) {
        if (Objects.isNull(values)) {
            values = Collections.emptyMap();
        }

        this.values = values.entrySet().stream()
            .filter(entry -> !Objects.isNull(entry.getKey()))
            .filter(entry -> !Objects.isNull(entry.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public static ObjectRepository empty() {
        return new ObjectRepository(new HashMap<>());
    }

    public ReadOnlyObjectRepository lock() {
        return new ReadOnlyObjectRepository(this.values);
    }

    public Object get(Class<?> key) {
        return values.get(key);
    }

    public void put(Class<?> key, Object value) {
        values.put(key, value);
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
