package filter.chain;

import filter.FilterWorker;
import java.util.Objects;
import vo.HttpRequest;
import vo.HttpResponse;

public class BaseFilterWorkerChain implements FilterWorkerChain {
    private final FilterWorkerChain nextFilterWorkerChain;
    private final FilterWorker filterWorker;

    public BaseFilterWorkerChain(FilterWorkerChain nextFilterWorkerChain, FilterWorker filterWorker) {
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
