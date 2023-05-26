package filter;

import java.util.Arrays;
import java.util.Objects;
import processor.HttpRequestExecutor;
import vo.HttpRequest;
import vo.HttpResponse;

public class ApplicationFilterChain implements FilterChain {
    private final HttpRequestExecutor executor;
    private final Filter[] filters;
    private int index = 0;

    public ApplicationFilterChain(HttpRequestExecutor executor, Filter[] filters) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(filters);
        this.executor = executor;
        this.filters = Arrays.stream(filters).filter(Objects::nonNull).toArray(Filter[]::new);
    }

    @Override
    public void doChain(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        if (index < filters.length) {
            Filter filter = filters[index++];
            filter.doChain(request, response, this);
        }

        executor.execute(request, response);
    }
}
