package task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import vo.HttpRequest;
import vo.HttpResponse;

public class PostTasks {
    private List<PostTask> values;

    public PostTasks(List<PostTask> values) {
        Objects.requireNonNull(values);
        this.values = values.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static PostTasks empty() {
        return new PostTasks(Collections.emptyList());
    }

    public PostTasks merge(PostTasks otherPostTasks) {
        if (Objects.isNull(otherPostTasks)) {
            return this;
        }

        this.values.addAll(otherPostTasks.values);
        return this;
    }

    public PostTasks add(PostTask filter) {
        if (Objects.isNull(filter)) {
            return this;
        }

        this.values.add(filter);
        return this;
    }

    public List<PostTask> getValues() {
        return new ArrayList<>(values);
    }

    public ReadOnlyPostTasks lock() {
        return new ReadOnlyPostTasks(this.values);
    }

    private static class MatchedPostTask {
        private final String preTaskName;
        private final PostTaskWorker postTaskWorker;

        public MatchedPostTask(String preTaskName, PostTaskWorker postTaskWorker) {
            if (Objects.isNull(preTaskName) || preTaskName.isBlank()) {
                throw new RuntimeException("filterName is empty.");
            }
            Objects.requireNonNull(postTaskWorker);

            this.preTaskName = preTaskName;
            this.postTaskWorker = postTaskWorker;
        }

        public static MatchedPostTask from(PostTask filter) {
            Objects.requireNonNull(filter);

            String filterName = filter.getName();
            PostTaskWorker preTaskWorker = filter.getFilterWorker();

            return new MatchedPostTask(filterName, preTaskWorker);
        }

        public PostTaskWorker getFilterWorker() {
            return postTaskWorker;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MatchedPostTask that = (MatchedPostTask) o;
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
                ", filterWorker=" + postTaskWorker +
                '}';
        }
    }

    @Override
    public String toString() {
        return "Filters{" +
            "values=" + values +
            '}';
    }

    public static class ReadOnlyPostTasks {
        private final List<PostTask> values;

        public ReadOnlyPostTasks(List<PostTask> values) {
            Objects.requireNonNull(values);
            this.values = values.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        }

        public List<PostTaskWorker> findFilterWorkers(String requestUrl) {
            if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
                return Collections.emptyList();
            }

            List<MatchedPostTask> matchedPostTasks = values.stream()
                .filter(value -> value.isMatchUrl(requestUrl))
                .map(MatchedPostTask::from)
                .distinct()
                .collect(Collectors.toUnmodifiableList());

            return matchedPostTasks.stream()
                .map(MatchedPostTask::getFilterWorker)
                .collect(Collectors.toUnmodifiableList());
        }

        public void execute(HttpRequest request, HttpResponse response) {
            List<PostTaskWorker> preTaskWorkers = findFilterWorkers(request.getHttpRequestPath().getValue().toString());
            for (PostTaskWorker preTaskWorker : preTaskWorkers) {
                preTaskWorker.execute(request, response);
            }

        }
    }
}
