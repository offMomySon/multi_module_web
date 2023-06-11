package filter;

import java.util.List;
import java.util.Objects;
import processor.HttpRequestExecutor;

public class ApplicationFilterChainCreator {
    private final HttpRequestExecutor requestExecutor;
    private final Filters filters;

    public ApplicationFilterChainCreator(HttpRequestExecutor requestExecutor, Filters filters) {
        if (Objects.isNull(requestExecutor)) {
            throw new RuntimeException("requestExecutor is empty.");
        }
        if (Objects.isNull(filters)) {
            throw new RuntimeException("filters is empty.");
        }

        this.requestExecutor = requestExecutor;
        this.filters = filters;
    }

    public ApplicationFilterChain create(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            throw new RuntimeException("requestUrl is empty.");
        }

        List<FilterWorker2> filterWorkers = filters.findFilterWorkers(requestUrl);
        return new ApplicationFilterChain(requestExecutor, filterWorkers.toArray(FilterWorker2[]::new));
    }
}
