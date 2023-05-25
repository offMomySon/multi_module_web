package filter;

import java.util.Objects;
import processor.HttpRequestExecutor;
import vo.HttpRequest;
import vo.HttpResponse;


public class HttpRequestExecuteFilter implements Filter {
    private final HttpRequestExecutor httpRequestExecutor;

    public HttpRequestExecuteFilter(HttpRequestExecutor httpRequestExecutor) {
        Objects.requireNonNull(httpRequestExecutor);
        this.httpRequestExecutor = httpRequestExecutor;
    }

    @Override
    public void doChain(HttpRequest request, HttpResponse response, FilterChain chain) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);
        Objects.requireNonNull(chain);

        httpRequestExecutor.execute(request, response);

        chain.doChain(request, response);
    }
}
