package pretask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import vo.HttpRequest;
import vo.HttpResponse;

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

    private static class MatchedPreTask {
        private final String preTaskName;
        private final PreTaskWorker preTaskWorker;

        public MatchedPreTask(String preTaskName, PreTaskWorker preTaskWorker) {
            if (Objects.isNull(preTaskName) || preTaskName.isBlank()) {
                throw new RuntimeException("filterName is empty.");
            }
            Objects.requireNonNull(preTaskWorker);

            this.preTaskName = preTaskName;
            this.preTaskWorker = preTaskWorker;
        }

        public static MatchedPreTask from(PreTask filter) {
            Objects.requireNonNull(filter);

            String filterName = filter.getName();
            PreTaskWorker preTaskWorker = filter.getFilterWorker();

            return new MatchedPreTask(filterName, preTaskWorker);
        }

        public PreTaskWorker getFilterWorker() {
            return preTaskWorker;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MatchedPreTask that = (MatchedPreTask) o;
            return Objects.equals(preTaskName, that.preTaskName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(preTaskName);
        }

        @Override
        public String toString() {
            return "MatchedFilter{" +
                "filterName='" + preTaskName + '\'' +
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

            List<MatchedPreTask> matchedPreTasks = values.stream()
                .filter(value -> value.isMatchUrl(requestUrl))
                .map(MatchedPreTask::from)
                .distinct()
                .collect(Collectors.toUnmodifiableList());

            return matchedPreTasks.stream()
                .map(MatchedPreTask::getFilterWorker)
                .collect(Collectors.toUnmodifiableList());
        }

        public void execute(HttpRequest request, HttpResponse response) {
            List<PreTaskWorker> preTaskWorkers = findFilterWorkers(request.getHttpRequestPath().getValue().toString());
            for (PreTaskWorker preTaskWorker : preTaskWorkers) {
                preTaskWorker.prevExecute(request, response);
            }

        }
    }
}
