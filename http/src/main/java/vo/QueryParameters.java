package vo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryParameters {
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String DEFUALT_VALUE = "";

    private final Map<String, String> values;

    public QueryParameters(Map<String, String> values) {
        Objects.requireNonNull(values);

        this.values = values.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> curr));
    }

    public static QueryParameters empty() {
        return new QueryParameters(new HashMap<>());
    }

    public static QueryParameters from(String queryParams) {
        if (Objects.isNull(queryParams) || queryParams.isBlank()) {
            return new QueryParameters(new HashMap<>());
        }

        String[] splitQueryParams = queryParams.split(PARAMETER_DELIMITER);
        log.info("splitQueryParams : {}", Arrays.toString(splitQueryParams));

        Map<String, String> newQueryParams = Arrays.stream(splitQueryParams)
            .map(QueryParameters::getQueryParamEntry)
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> curr));

        return new QueryParameters(newQueryParams);
    }

    private static Map.Entry<String, String> getQueryParamEntry(String queryParam) {
        String[] keyValue = queryParam.split(KEY_VALUE_DELIMITER, 2);
        return Map.entry(keyValue[0], keyValue[1]);
    }

    public Set<String> keySet() {
        return values.keySet();
    }

    public String getValue(String key) {
        if (Objects.isNull(key) || key.isBlank()) {
            return DEFUALT_VALUE;
        }
        return values.getOrDefault(key, DEFUALT_VALUE);
    }

    public Map<String, String> getParameterMap() {
        return new HashMap<>(values);
    }
}
