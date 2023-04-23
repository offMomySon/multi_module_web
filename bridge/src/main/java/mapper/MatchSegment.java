package mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import vo.RequestValues;

public class MatchSegment {
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";

    private final Map<String, String> values;

    public MatchSegment(Map<String, String> values) {
        this.values = values.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (curr, prev) -> prev));
    }

    public static MatchSegment empty() {
        return new MatchSegment(new HashMap<>());
    }

    public boolean containKey(String key) {
        if (Objects.isNull(key) || key.isBlank()) {
            return false;
        }
        return values.containsKey(key);
    }

    public String get(String key) {
        if (Objects.isNull(key) || key.isBlank()) {
            throw new RuntimeException("key is empty.");
        }
        return values.get(key);
    }

    public String put(String key, String value) {
        if (Objects.isNull(key) || key.isBlank()) {
            throw new RuntimeException("key is empty.");
        }
        if (Objects.isNull(value) || value.isBlank()) {
            throw new RuntimeException("value is empty.");
        }

        return values.put(key, value);
    }

    public MatchSegment merge(MatchSegment other) {
        Map<String, String> newValues = new HashMap<>();

        values.forEach((key, value) -> newValues.merge(key, value, (prev, curr) -> prev));
        other.values.forEach((key, value) -> newValues.merge(key, value, (prev, curr) -> prev));

        return new MatchSegment(newValues);
    }

    public RequestValues extractPathVariable() {
        Map<String, String> pathVariables = this.values.entrySet().stream()
            .filter(entry -> isPathVariable(entry.getKey()))
            .map(entry -> {
                String key = entry.getKey();
                String uncapsulatedKey = key.substring(1, key.length() - 1);
                String value = entry.getValue();

                return Map.entry(uncapsulatedKey, value);
            })
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        return new RequestValues(pathVariables);
    }

    private static boolean isPathVariable(String segment) {
        return segment.startsWith(PATH_VARIABLE_OPENER) && segment.endsWith(PATH_VARIABLE_CLOSER);
    }
}
