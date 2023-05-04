package mapper.segmentv3;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PathVariable {
    private final Map<String, String> values;

    public PathVariable(Map<String, String> values) {
        Objects.requireNonNull(values);
        this.values = values.entrySet()
            .stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public static PathVariable empty() {
        return new PathVariable(new HashMap<>());
    }

    public String get(String key) {
        return values.get(key);
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public void put(String key, String value) {
        values.put(key, value);
    }

    public PathVariable copy() {
        return new PathVariable(this.values);
    }

    public void clear() {
        values.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathVariable that = (PathVariable) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    public void merge(PathVariable otherPathVariable) {
        Objects.requireNonNull(otherPathVariable);

        otherPathVariable.values
            .forEach((key, value) -> this.values.merge(key, value, (prev, curr) -> prev));
    }

    @Override
    public String toString() {
        return "PathVariable{" +
            "values=" + values +
            '}';
    }
}
