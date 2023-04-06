package beanContainer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ComponentContainer {
    private final Map<Class<?>, Object> values;

    public ComponentContainer() {
        this.values = new HashMap<>();
    }

    public ComponentContainer(Map<Class<?>, Object> values) {
        this.values = values.entrySet().stream()
            .filter(entry -> !Objects.isNull(entry.getKey()))
            .filter(entry -> !Objects.isNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public ComponentContainer merge(ComponentContainer otherContainer) {
        if (Objects.isNull(otherContainer)) {
            return this;
        }

        otherContainer.values
            .forEach((key, value) -> this.values.merge(key, value, (prev, curr) -> prev));

        return this;
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
}
