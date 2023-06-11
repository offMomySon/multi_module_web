package filter;

import java.util.Arrays;
import java.util.Objects;
import processor.HttpRequestExecutor;
import vo.HttpRequest;
import vo.HttpResponse;

public class ApplicationFilterChain implements FilterChain {
    private final HttpRequestExecutor executor;
    private final FilterWorker2[] filterWorkers2;
    private int index = 0;

    public ApplicationFilterChain(HttpRequestExecutor executor, FilterWorker2[] filterWorkers2) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(filterWorkers2);
        this.executor = executor;
        this.filterWorkers2 = Arrays.stream(filterWorkers2).filter(Objects::nonNull).toArray(FilterWorker2[]::new);
    }

    @Override
    public void doChain(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        if (index < filterWorkers2.length) {
            FilterWorker2 filterWorker2 = filterWorkers2[index++];
//            filterWorker2.doChain(request, response, this);
        }

        executor.execute(request, response);
    }
}
