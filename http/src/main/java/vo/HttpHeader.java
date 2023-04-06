package vo;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import static java.util.Objects.nonNull;

public class HttpHeader {
    private static final String HEADER_KEY_VALUE_DELIMITER = ":";
    private static final String HEADER_VALUE_DELIMITER = ",";
    private static final String END_OF_LINE = "\r\n";

    private final Map<String, Set<String>> value;

    private HttpHeader(Map<String, Set<String>> value) {
        Objects.requireNonNull(value);
        
        this.value = createFilteredNoneValidAndUnmodifiable(value);
    }

    public Set<String> getKeys() {
        return value.keySet();
    }

    public Set<String> getValues(String key) {
        if (StringUtils.isEmpty(key) || StringUtils.isBlank(key)) {
            throw new RuntimeException(MessageFormat.format("key is invalid : `{}`", key));
        }

        return value.get(key);
    }

    public String generateHeaderMessage() {
        return value.entrySet().stream()
            .map(HttpHeader::generateHeaderLineMessage)
            .collect(Collectors.joining(END_OF_LINE));
    }

    private static String generateHeaderLineMessage(Map.Entry<String, Set<String>> headerEntry) {
        String key = headerEntry.getKey();
        String values = String.join(HEADER_VALUE_DELIMITER, headerEntry.getValue());

        StringBuilder headerLineMessageBuilder = new StringBuilder();
        headerLineMessageBuilder.append(key);
        headerLineMessageBuilder.append(HEADER_KEY_VALUE_DELIMITER);
        headerLineMessageBuilder.append(values);

        return headerLineMessageBuilder.toString();
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
            .filter(HttpHeader::isValid)
            .collect(Collectors.toUnmodifiableSet());
    }

    private static Set<String> merge(Set<String> prevValue, Set<String> value) {
        return Stream.concat(prevValue.stream(), value.stream()).collect(Collectors.toUnmodifiableSet());
    }

    private static boolean isValid(String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isBlank(value)) {
            return false;
        }
        return true;
    }

    public static HttpHeader.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, Set<String>> value = new HashMap<>();

        private Builder() {
        }

        public Builder append(String headerLine) {
            if (StringUtils.isEmpty(headerLine) || StringUtils.isBlank(headerLine)) {
                throw new RuntimeException(MessageFormat.format("headerLine is invalid : `{}`", headerLine));
            }

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
