package matcher.path;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import static java.util.Objects.isNull;

@EqualsAndHashCode
public class PathVariable {
    private final Map<String, String> values;

    public PathVariable(Map<String, String> values) {
        if (isNull(values)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }

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

    public Map<String, String> getValues() {
        return new HashMap<>(values);
    }

    public PathVariable copy() {
        return new PathVariable(this.values);
    }

    public void clear() {
        values.clear();
    }

    public PathVariable merge(PathVariable otherPathVariable) {
        Objects.requireNonNull(otherPathVariable);

        Map<String, String> newMap = new HashMap<>(this.values);
        otherPathVariable.values
            .forEach((key, value) -> newMap.merge(key, value, (prev, curr) -> prev));

        return new PathVariable(newMap);
    }

    @Override
    public String toString() {
        return "PathVariable{" +
            "values=" + values +
            '}';
    }
}