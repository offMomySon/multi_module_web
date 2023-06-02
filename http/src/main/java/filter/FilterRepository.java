package filter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilterRepository {
    private final List<FilterDef> filterDefs;
    private final Map<String, Filter> filtersMapByName;

    public FilterRepository(List<FilterDef> filterDefs, Map<String, Filter> filtersMapByName) {
        Objects.requireNonNull(filterDefs);
        Objects.requireNonNull(filtersMapByName);

        this.filterDefs = filterDefs.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());
        this.filtersMapByName = filtersMapByName.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public static FilterRepository from(List<FilterRegistration> filterRegistrations) {
        Objects.requireNonNull(filterRegistrations);
        filterRegistrations = filterRegistrations.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());

        List<FilterDef> filterDefs = filterRegistrations.stream()
            .map(r -> new FilterDef(r.getFilterName(), r.getPatterns()))
            .collect(Collectors.toUnmodifiableList());
        Map<String, Filter> filtersMapByName = filterRegistrations.stream()
            .map(r -> Map.entry(r.getFilterName(), r.getFilter()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        return new FilterRepository(filterDefs, filtersMapByName);
    }

//    public List<String> findMatchFilterNames(String requestPattern) {
//        if (Objects.isNull(requestPattern) || requestPattern.isBlank()) {
//            throw new RuntimeException("requestPattern is empty.");
//        }
//
//
//    }


}
