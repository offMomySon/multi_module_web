package structure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import static validate.ValidateUtil.validateNull;

@Slf4j
public class HttpHeader {
    private static final String HEADER_LINE_DELIMITER = ":";
    private static final String HEADER_VALUE_DELIMITER = ",";

    private final Map<String, Set<String>> headers;

    public boolean isExistKey(String key){
        return headers.containsKey(key);
    }

    public Set<String> getHeaderKeys() {
        return headers.keySet().stream().collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> getHeaderValue(String key) {
        return headers.get(key).stream().collect(Collectors.toUnmodifiableSet());
    }

    private HttpHeader(Map<String, Set<String>> headers) {
        validateNull(headers);

        Map<String, Set<String>> newHeaders = headers.entrySet().stream()
            .map(e -> Map.entry(e.getKey(), e.getValue().stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet())))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        this.headers = newHeaders;
    }

    public static HttpHeader from(List<String> headers) {
        Map<String, Set<String>> newHeader = new HashMap<>();

        for (String header : headers) {
            String[] delimitedHeader = header.split(HEADER_LINE_DELIMITER, 2);

            String headerKey = delimitedHeader[0];
            Set<String> headerValues = Arrays.stream(delimitedHeader[1].split(HEADER_VALUE_DELIMITER)).collect(Collectors.toUnmodifiableSet());

            newHeader.put(headerKey, headerValues);
        }

        return new HttpHeader(newHeader);
    }


}
