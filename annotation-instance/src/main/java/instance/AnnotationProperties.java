package instance;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnotationProperties {
    private final Map<String, Object> values;

    public AnnotationProperties(Map<String, Object> values) {
        Objects.requireNonNull(values);
        this.values = values.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public static AnnotationProperties empty() {
        return new AnnotationProperties(Collections.emptyMap());
    }

    public Object getValue(String property) {
        Objects.requireNonNull(property);

        if (!values.containsKey(property)) {
            throw new RuntimeException("does not contain property");
        }
        return values.get(property);
    }

    public Object getValueOrDefault(String property, Object defaultValue) {
        Objects.requireNonNull(property);
        Objects.requireNonNull(defaultValue);

        if (!values.containsKey(property)) {
            return defaultValue;
        }
        return values.get(property);
    }

    public boolean contain(String property) {
        if (Objects.isNull(property)) {
            return false;
        }
        return values.containsKey(property);
    }
}