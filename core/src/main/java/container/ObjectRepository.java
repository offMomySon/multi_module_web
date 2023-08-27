package container;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectRepository {
    private final Map<Class<?>, Object> values;

    private ObjectRepository() {
        this.values = new HashMap<>();
    }

    public ObjectRepository(Map<Class<?>, Object> values) {
        this.values = values.entrySet().stream()
                .filter(entry -> !Objects.isNull(entry.getKey()))
                .filter(entry -> !Objects.isNull(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public static ObjectRepository empty() {
        return new ObjectRepository(new HashMap<>());
    }

    public ObjectRepository merge(ObjectRepository otherObjectRepository) {
        if (Objects.isNull(otherObjectRepository)) {
            return this;
        }

        Stream<Map.Entry<Class<?>, Object>> baseValueStream = this.values.entrySet().stream();
        Stream<Map.Entry<Class<?>, Object>> otherValueStream = otherObjectRepository.values.entrySet().stream();

        Map<Class<?>, Object> newValue = Stream.concat(baseValueStream, otherValueStream)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        return new ObjectRepository(newValue);
    }

    public ObjectRepository lock() {
        return new ObjectRepository(Collections.unmodifiableMap(this.values));
    }

    public Set<Class<?>> keySet() {
        return new HashSet<>(values.keySet());
    }

    public Object get(Class<?> key) {
        return values.get(key);
    }

    public Optional<Object> getOptional(Class<?> key) {
        if(!values.containsKey(key)){
            return Optional.empty();
        }

        Object object = values.get(key);
        return Optional.of(object);
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
