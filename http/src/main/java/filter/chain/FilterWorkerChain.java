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
    public boolean execute(HttpRequest request, HttpResponse response) {
        boolean next = filterWorker.prevExecute(request, response);
        if (!next) {
            return false;
        }

        next = nextFilterChain.execute(request, response);
        if (!next) {
            return false;
        }

        next = filterWorker.postExecute(request, response);
        if (!next) {
            return false;
        }
        return true;
    }
}
