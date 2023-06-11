package filter.chain;

import java.util.Objects;
import processor.HttpRequestExecutor;
import vo.HttpRequest;
import vo.HttpResponse;


public class HttpRequestExecutorChain implements FilterWorkerChain {
    private final HttpRequestExecutor httpRequestExecutor;

    public HttpRequestExecutorChain(HttpRequestExecutor httpRequestExecutor) {
        Objects.requireNonNull(httpRequestExecutor);
        this.httpRequestExecutor = httpRequestExecutor;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        httpRequestExecutor.execute(request, response);
    }
}
