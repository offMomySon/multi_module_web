package instance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;

// 역할.
// instance graph 생성용 dto.
public class ObjectGraph {
    private final Map<Class<?>, Object> values;

    public ObjectGraph(@NonNull Map<Class<?>, Object> values) {
        this.values = Map.copyOf(values);
    }

    public static ObjectGraph empty() {
        return new ObjectGraph(Collections.emptyMap());
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

    public ReadOnlyObjectGraph lock() {
        return new ReadOnlyObjectGraph(this.values);
    }

    public static class ReadOnlyObjectGraph {
        private final Map<Class<?>, Object> values;

        public ReadOnlyObjectGraph(Map<Class<?>, Object> values) {
            if (Objects.isNull(values)) {
                values = Collections.emptyMap();
            }

            this.values = values.entrySet().stream()
                .filter(entry -> !Objects.isNull(entry.getKey()))
                .filter(entry -> !Objects.isNull(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
        }

        public static ReadOnlyObjectGraph empty() {
            return new ReadOnlyObjectGraph(Collections.emptyMap());
        }

        public Object get(Class<?> key) {
            return values.get(key);
        }

        public boolean containsKey(Class<?> key) {
            return values.containsKey(key);
        }

        public ReadOnlyObjectGraph merge(ReadOnlyObjectGraph other) {
            if (Objects.isNull(other)) {
                return this;
            }

            Stream<Map.Entry<Class<?>, Object>> baseValueStream = this.values.entrySet().stream();
            Stream<Map.Entry<Class<?>, Object>> otherValueStream = other.values.entrySet().stream();
            Map<Class<?>, Object> mergedValues = Stream.concat(baseValueStream, otherValueStream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

            return new ReadOnlyObjectGraph(mergedValues);
        }

        public Map<Class<?>, Object> copyValues() {
            return Map.copyOf(this.values);
        }
    }
}
