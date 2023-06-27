package container;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Container {
    private final Map<Class<?>, Object> values;

    private Container() {
        this.values = new HashMap<>();
    }

    public Container(Map<Class<?>, Object> values) {
        this.values = values.entrySet().stream()
                .filter(entry -> !Objects.isNull(entry.getKey()))
                .filter(entry -> !Objects.isNull(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public static Container empty() {
        return new Container(new HashMap<>());
    }

    public Container merge(Container otherContainer) {
        if (Objects.isNull(otherContainer)) {
            return this;
        }

        Stream<Map.Entry<Class<?>, Object>> baseValueStream = this.values.entrySet().stream();
        Stream<Map.Entry<Class<?>, Object>> otherValueStream = otherContainer.values.entrySet().stream();

        Map<Class<?>, Object> newValue = Stream.concat(baseValueStream, otherValueStream)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        return new Container(newValue);
    }

    public Container lock() {
        return new Container(Collections.unmodifiableMap(this.values));
    }

    public Set<Class<?>> keySet() {
        return new HashSet<>(values.keySet());
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

    public static class ReadOnlyContainer {
        private final Map<Class<?>, Object> values;

        public ReadOnlyContainer(Map<Class<?>, Object> values) {
            this.values = values.entrySet().stream()
                    .filter(entry -> !Objects.isNull(entry.getKey()))
                    .filter(entry -> !Objects.isNull(entry.getValue()))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
        }
    }

    @Override
    public String toString() {
        return "Container{" +
            "values=" + values +
            '}';
    }
}
