package vo;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestParameters {
    private final Map<String, String> values;

    public RequestParameters(Map<String, String> values) {
        if (Objects.isNull(values)) {
            throw new RuntimeException("values is null.");
        }

        Map<String, String> newValues =
            values.entrySet().stream()
                .filter(entry -> !Objects.isNull(entry.getKey()))
                .filter(entry -> !Objects.isNull(entry.getValue()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        this.values = newValues;
    }

    public static RequestParameters empty() {
        return new RequestParameters(Collections.emptyMap());
    }
}
