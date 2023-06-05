package filter;

import java.util.Arrays;
import java.util.Objects;
import processor.HttpRequestExecutor;
import vo.HttpRequest;
import vo.HttpResponse;

public class ApplicationFilterChain implements FilterChain {
    private final HttpRequestExecutor executor;
    private final FilterWorker[] filterWorkers;
    private int index = 0;

    public ApplicationFilterChain(HttpRequestExecutor executor, FilterWorker[] filterWorkers) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(filterWorkers);
        this.executor = executor;
        this.filterWorkers = Arrays.stream(filterWorkers).filter(Objects::nonNull).toArray(FilterWorker[]::new);
    }

    @Override
    public void doChain(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        if (index < filterWorkers.length) {
            FilterWorker filterWorker = filterWorkers[index++];
            filterWorker.doChain(request, response, this);
        }

        executor.execute(request, response);
    }
}
