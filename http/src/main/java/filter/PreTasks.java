package filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PreTasks {
    private List<PreTask> values;

    public PreTasks(List<PreTask> values) {
        Objects.requireNonNull(values);
        this.values = values.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static PreTasks empty() {
        return new PreTasks(Collections.emptyList());
    }

    public PreTasks merge(PreTasks otherPreTasks) {
        if (Objects.isNull(otherPreTasks)) {
            return this;
        }

        this.values.addAll(otherPreTasks.values);
        return this;
    }

    public PreTasks add(PreTask filter) {
        if (Objects.isNull(filter)) {
            return this;
        }

        this.values.add(filter);
        return this;
    }

    public List<PreTask> getValues() {
        return new ArrayList<>(values);
    }

    public ReadOnlyPreTasks lock() {
        return new ReadOnlyPreTasks(this.values);
    }

    private static class MatchedFilter {
        private final String filterName;
        private final PreTaskWorker preTaskWorker;

        public MatchedFilter(String filterName, PreTaskWorker preTaskWorker) {
            if (Objects.isNull(filterName) || filterName.isBlank()) {
                throw new RuntimeException("filterName is empty.");
            }
            Objects.requireNonNull(preTaskWorker);

            this.filterName = filterName;
            this.preTaskWorker = preTaskWorker;
        }

        public static MatchedFilter from(PreTask filter) {
            Objects.requireNonNull(filter);

            String filterName = filter.getName();
            PreTaskWorker preTaskWorker = filter.getFilterWorker();

            return new MatchedFilter(filterName, preTaskWorker);
        }

        public PreTaskWorker getFilterWorker() {
            return preTaskWorker;
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
                ", filterWorker=" + preTaskWorker +
                '}';
        }
    }

    @Override
    public String toString() {
        return "Filters{" +
            "values=" + values +
            '}';
    }

    public static class ReadOnlyPreTasks {
        private final List<PreTask> values;

        public ReadOnlyPreTasks(List<PreTask> values) {
            Objects.requireNonNull(values);
            this.values = values.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        }

        public List<PreTaskWorker> findFilterWorkers(String requestUrl) {
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
