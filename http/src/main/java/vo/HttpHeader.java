package vo;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import validate.ValidateUtil;
import static java.util.Objects.nonNull;
import static validate.ValidateUtil.isValid;
import static validate.ValidateUtil.validateNull;

public class HttpHeader {
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
        ValidateUtil.validate(key);

        return value.get(key);
    }
}
