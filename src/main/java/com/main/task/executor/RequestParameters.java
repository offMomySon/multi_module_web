package com.main.task.executor;

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

    public static RequestParameters empty() {
        return new RequestParameters(Collections.emptyMap());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestParameters that = (RequestParameters) o;
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
