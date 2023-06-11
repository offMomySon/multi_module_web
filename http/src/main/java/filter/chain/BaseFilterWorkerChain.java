package filter.chain;

import filter.FilterWorker2;
import java.util.Objects;
import vo.HttpRequest;
import vo.HttpResponse;

public class BaseFilterWorkerChain implements FilterWorkerChain {
    private final FilterWorkerChain nextFilterWorkerChain;
    private final FilterWorker2 filterWorker;

    public BaseFilterWorkerChain(FilterWorkerChain nextFilterWorkerChain, FilterWorker2 filterWorker) {
        Objects.requireNonNull(nextFilterWorkerChain);
        Objects.requireNonNull(filterWorker);
        this.nextFilterWorkerChain = nextFilterWorkerChain;
        this.filterWorker = filterWorker;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        filterWorker.prevExecute(request, response);

        nextFilterWorkerChain.execute(request, response);

        filterWorker.postExecute(request, response);
    }
}
