package filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Filters {
    private List<Filter> values;

    public Filters(List<Filter> values) {
        Objects.requireNonNull(values);
        this.values = values.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static Filters empty() {
        return new Filters(Collections.emptyList());
    }

    public Filters merge(Filters otherFilters) {
        if (Objects.isNull(otherFilters)) {
            return this;
        }

        this.values.addAll(otherFilters.values);
        return this;
    }

    public Filters add(Filter filter) {
        if (Objects.isNull(filter)) {
            return this;
        }

        this.values.add(filter);
        return this;
    }

    public List<Filter> getValues() {
        return new ArrayList<>(values);
    }

    public ReadOnlyFilters lock() {
        return new ReadOnlyFilters(this.values);
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
            FilterWorker filterWorker = filter.getFilterWorker2();

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

        @Override
        public String toString() {
            return "MatchedFilter{" +
                "filterName='" + filterName + '\'' +
                ", filterWorker=" + filterWorker +
                '}';
        }
    }

    @Override
    public String toString() {
        return "Filters{" +
            "values=" + values +
            '}';
    }

    public static class ReadOnlyFilters {
        private final List<Filter> values;

        public ReadOnlyFilters(List<Filter> values) {
            Objects.requireNonNull(values);
            this.values = values.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        }

        public List<FilterWorker> findFilterWorkers(String requestUrl) {
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
    }
}
