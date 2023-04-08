package vo;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParameterValues {
    private final Map<String, String> values;

    public ParameterValues(Map<String, String> values) {
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

    public static ParameterValues empty() {
        return new ParameterValues(Collections.emptyMap());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterValues that = (ParameterValues) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
