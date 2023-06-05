package filter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FilterRepository {
    private final List<FilterDef> filterDefs;
    private final Map<String, FilterWorker> filtersMapByName;

    public FilterRepository(List<FilterDef> filterDefs, Map<String, FilterWorker> filtersMapByName) {
        Objects.requireNonNull(filterDefs);
        Objects.requireNonNull(filtersMapByName);

        filterDefs = filterDefs.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        filtersMapByName = filtersMapByName.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        if (filterDefs.isEmpty()) {
            throw new RuntimeException("filterDefs is empty.");
        }
        if (filtersMapByName.isEmpty()) {
            throw new RuntimeException("filterDefs is empty.");
        }

        this.filterDefs = filterDefs;
        this.filtersMapByName = filtersMapByName;
    }

    public static FilterRepository from(List<FilterRegistration> filterRegistrations) {
        Objects.requireNonNull(filterRegistrations);
        filterRegistrations = filterRegistrations.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());

        List<FilterDef> filterDefs = filterRegistrations.stream()
            .map(r -> FilterDef.of(r.getFilterName(), r.getPatterns()))
            .collect(Collectors.toUnmodifiableList());
        Map<String, FilterWorker> filtersMapByName = filterRegistrations.stream()
            .map(r -> Map.entry(r.getFilterName(), r.getFilter()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        return new FilterRepository(filterDefs, filtersMapByName);
    }

    public List<String> findUrlMatchFilterNames(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            throw new RuntimeException("requestPattern is empty.");
        }

        return filterDefs.stream()
            .map(filterDef -> filterDef.matchUrl(requestUrl))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<FilterWorker> findFilterByNames(List<String> names) {
        Objects.requireNonNull(names);
        List<String> newNames = names.stream()
            .filter(Objects::nonNull)
            .filter(name -> !name.isBlank())
            .collect(Collectors.toUnmodifiableList());

        return newNames.stream()
            .filter(filtersMapByName::containsKey)
            .map(filtersMapByName::get)
            .collect(Collectors.toUnmodifiableList());
    }
}
