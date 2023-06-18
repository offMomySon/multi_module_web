package filter.chain;

import filter.FilterWorker;
import java.util.Objects;
import vo.HttpRequest;
import vo.HttpResponse;

public class FilterWorkerChain implements FilterChain {
    private final FilterChain nextFilterChain;
    private final FilterWorker filterWorker;

    public FilterWorkerChain(FilterChain nextFilterChain, FilterWorker filterWorker) {
        Objects.requireNonNull(nextFilterChain);
        Objects.requireNonNull(filterWorker);
        this.nextFilterChain = nextFilterChain;
        this.filterWorker = filterWorker;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        filterWorker.prevExecute(request, response);

        nextFilterChain.execute(request, response);

        filterWorker.postExecute(request, response);
    }
}
