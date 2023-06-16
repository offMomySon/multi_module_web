package filter.chain;

import java.util.Objects;
import processor.HttpRequestExecutor;
import vo.HttpRequest;
import vo.HttpResponse;


public class HttpRequestExecutorChain implements FilterWorkerChain {
    private final HttpRequestExecutor httpStaticResourceExecutor;
    private final HttpRequestExecutor httpRequestExecutor;
//    private final HttpSta


    public HttpRequestExecutorChain(HttpRequestExecutor httpStaticResourceExecutor, HttpRequestExecutor httpRequestExecutor) {
        Objects.requireNonNull(httpStaticResourceExecutor);
        Objects.requireNonNull(httpRequestExecutor);
        this.httpStaticResourceExecutor = httpStaticResourceExecutor;
        this.httpRequestExecutor = httpRequestExecutor;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        boolean execute = httpStaticResourceExecutor.execute(request, response);
        if (execute) {
            return;
        }

        httpRequestExecutor.execute(request, response);
    }
}
