package matcher.segment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PathVariableValue {
    private final Map<String, String> values;

    public PathVariableValue(Map<String, String> values) {
        Objects.requireNonNull(values);
        this.values = values.entrySet()
            .stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public static PathVariableValue empty() {
        return new PathVariableValue(new HashMap<>());
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

    public Map<String, String> getValues() {
        return new HashMap<>(values);
    }

    public PathVariableValue copy() {
        return new PathVariableValue(this.values);
    }

    public void clear() {
        values.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathVariableValue that = (PathVariableValue) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    public PathVariableValue merge(PathVariableValue otherPathVariableValue) {
        Objects.requireNonNull(otherPathVariableValue);

        Map<String, String> newMap = new HashMap<>(this.values);
        otherPathVariableValue.values
            .forEach((key, value) -> newMap.merge(key, value, (prev, curr) -> prev));

        return new PathVariableValue(newMap);
    }

    @Override
    public String toString() {
        return "PathVariable{" +
            "values=" + values +
            '}';
    }
}
