package vo;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpHeader {
    private static final String HEADER_KEY_VALUE_DELIMITER = ":";
    private static final String DEFAULT_VALUE = "";

    private final Map<String, String> values;

    public HttpHeader(Map<String, String> values) {
        Objects.requireNonNull(values);

        this.values = values.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public String get(String key) {
        if (Objects.isNull(key) || key.isBlank()) {
            return DEFAULT_VALUE;
        }
        return values.getOrDefault(key, DEFAULT_VALUE);
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
            "values=" + values +
            '}';
    }

    public Set<String> keySet() {
        return values.keySet();
    }

    public static HttpHeader.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, String> values;

        private Builder() {
            values = new HashMap<>();
        }

        public Builder append(String headerLine) {
            if (Objects.isNull(headerLine) || headerLine.isBlank()) {
                throw new RuntimeException(MessageFormat.format("headerLine is invalid : `{}`", headerLine));
            }

            String[] headerElements = headerLine.split(HEADER_KEY_VALUE_DELIMITER, 2);

            String key = headerElements[0];
            String value = headerElements[1];

            values.put(key, value);

            return this;
        }

        public HttpHeader build() {
            return new HttpHeader(this.values);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpHeader that = (HttpHeader) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
