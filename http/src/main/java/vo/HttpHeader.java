package vo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import validate.ValidateUtil;
import static java.util.Objects.nonNull;
import static validate.ValidateUtil.isValid;
import static validate.ValidateUtil.validate;
import static validate.ValidateUtil.validateNull;

public class HttpHeader {
    private static final String HEADER_KEY_VALUE_DELIMITER = ":";
    private static final String HEADER_VALUE_DELIMITER = ",";

    private final Map<String, Set<String>> value;

    public HttpHeader(Map<String, Set<String>> value) {
        this.value = createFilteredNoneValidAndUnmodifiable(validateNull(value));
    }

    private static Map<String, Set<String>> createFilteredNoneValidAndUnmodifiable(Map<String, Set<String>> value) {
        return value.entrySet().stream()
            .filter(es -> isValid(es.getKey()))
            .filter(es -> nonNull(es.getValue()))
            .map(e -> Map.entry(e.getKey(), createFilteredNoneValidAndUnmodifiable(e.getValue())))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, HttpHeader::merge));
    }

    private static Set<String> createFilteredNoneValidAndUnmodifiable(Set<String> value) {
        return value.stream()
            .filter(ValidateUtil::isValid)
            .collect(Collectors.toUnmodifiableSet());
    }

    private static Set<String> merge(Set<String> prevValue, Set<String> value) {
        return Stream.concat(prevValue.stream(), value.stream()).collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> getKeys() {
        return value.keySet();
    }

    public Set<String> getValues(String key) {
        validate(key);

        return value.get(key);
    }

    public HttpHeader.Builder builder() {
        return new Builder();
    }

    private static class Builder {
        private final Map<String, Set<String>> value = new HashMap<>();

        public Builder() {
        }

        public Builder append(String headerLine) {
            validate(headerLine);

            String[] splitHeader = headerLine.split(HEADER_KEY_VALUE_DELIMITER, 2);

            String headerKey = splitHeader[0];
            Set<String> headerValue = Arrays.stream(splitHeader[1].split(HEADER_VALUE_DELIMITER)).map(String::trim).collect(Collectors.toUnmodifiableSet());

            value.put(headerKey, headerValue);

            return this;
        }

        public HttpHeader build() {
            return new HttpHeader(this.value);
        }
    }
}
