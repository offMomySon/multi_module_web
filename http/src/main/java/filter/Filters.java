package filter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import static java.util.function.Function.identity;

public class Filters {
    private final Map<String, Filter> values;

    public Filters(Map<String, Filter> values) {
        Objects.requireNonNull(values);

        this.values = values.entrySet().stream()
            .filter(e -> Objects.nonNull(e.getKey()))
            .filter(e -> Objects.nonNull(e.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public static Filters from(List<Filter> filters) {
        Objects.requireNonNull(filters);
        filters = filters.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        if (filters.isEmpty()) {
            throw new RuntimeException("filters is empty.");
        }

        Map<String, Filter> values = filters.stream().collect(Collectors.toUnmodifiableMap(Filter::getName, identity(), (prev, curr) -> prev));
        return new Filters(values);
    }

    public List<FilterWorker> findMatchFilterWorkers(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            throw new RuntimeException("requestPattern is empty.");
        }

        List<String> matchedDistinctFilterNames = values.values().stream()
            .filter(value -> value.isMatchUrl(requestUrl))
            .map(Filter::getName)
            .distinct()
            .collect(Collectors.toUnmodifiableList());

        return matchedDistinctFilterNames.stream()
            .map(values::get)
            .map(Filter::getFilterWorker)
            .collect(Collectors.toUnmodifiableList());
    }
}
