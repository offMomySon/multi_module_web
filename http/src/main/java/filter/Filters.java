package filter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Filters {
    private final List<Filter> values;

    public Filters(List<Filter> values) {
        Objects.requireNonNull(values);
        values = values.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        if (values.isEmpty()) {
            throw new RuntimeException("filters is empty.");
        }

        this.values = values;
    }

    public Filters merge(Filters otherFilters) {
        if (Objects.isNull(otherFilters)) {
            return this;
        }

        Set<String> otherFilterNames = otherFilters.values.stream()
            .map(Filter::getName)
            .collect(Collectors.toUnmodifiableSet());

        Optional<String> optionalDuplicatFilterName = this.values.stream().map(Filter::getName).filter(otherFilterNames::contains).findAny();
        if (optionalDuplicatFilterName.isPresent()) {
            String duplicateFilterName = optionalDuplicatFilterName.get();
            throw new RuntimeException(MessageFormat.format("Has duplicate filter name. FilterName : `{}`", duplicateFilterName));
        }

        List<Filter> mergedValues = Stream.of(this.values, otherFilters.values)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
        return new Filters(mergedValues);
    }

    // todo
    // 받은 입력이 null 이면 excpeiton 이 좋을까?, emtpy 가 좋을까?
    public List<FilterWorker> findMatchFilterWorkers(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            return Collections.emptyList();
        }

        List<MatchedFilter> matchedFilters = values.stream()
            .filter(value -> value.isMatchUrl(requestUrl))
            .map(MatchedFilter::from)
            .distinct()
            .collect(Collectors.toUnmodifiableList());

        return matchedFilters.stream()
            .map(MatchedFilter::getFilterWorker)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<Filter> getValues() {
        return new ArrayList<>(values);
    }

    private static class MatchedFilter {
        private final String filterName;
        private final FilterWorker filterWorker;

        public MatchedFilter(String filterName, FilterWorker filterWorker) {
            if (Objects.isNull(filterName) || filterName.isBlank()) {
                throw new RuntimeException("filterName is empty.");
            }
            Objects.requireNonNull(filterWorker);

            this.filterName = filterName;
            this.filterWorker = filterWorker;
        }

        public static MatchedFilter from(Filter filter) {
            Objects.requireNonNull(filter);

            String filterName = filter.getName();
            FilterWorker filterWorker = filter.getFilterWorker();

            return new MatchedFilter(filterName, filterWorker);
        }

        public FilterWorker getFilterWorker() {
            return filterWorker;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MatchedFilter that = (MatchedFilter) o;
            return Objects.equals(filterName, that.filterName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(filterName);
        }
    }
}
