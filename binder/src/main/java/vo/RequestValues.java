package vo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestValues {
    private final Map<String, String> values;

    public RequestValues(Map<String, String> values) {
        if (Objects.isNull(values)) {
            throw new RuntimeException("values is null.");
        }

        Map<String, String> newValues =
            values.entrySet().stream()
                .filter(entry -> !Objects.isNull(entry.getKey()))
                .filter(entry -> !Objects.isNull(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        this.values = newValues;
    }

    public void put(String key, String value) {
        values.put(key, value);
    }

    public void remove(String key) {
        values.remove(key);
    }

    public String get(String key) {
        return values.get(key);
    }

    public String getOrDefault(String key, String defaultValue) {
        String valueOrNull = get(key);

        if (Objects.isNull(valueOrNull)) {
            return defaultValue;
        }
        return valueOrNull;
    }

    public RequestValues merge(RequestValues other) {
        Map<String, String> newValues = new HashMap<>();

        values.forEach((key, value) -> newValues.merge(key, value, (prev, curr) -> prev));
        other.values.forEach((key, value) -> newValues.merge(key, value, (prev, curr) -> prev));

        return new RequestValues(newValues);
    }

    public static RequestValues empty() {
        return new RequestValues(Collections.emptyMap());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestValues that = (RequestValues) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "RequestValues{" +
            "values=" + values +
            '}';
    }
}
