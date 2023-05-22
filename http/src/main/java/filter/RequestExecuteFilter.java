package filter;

import java.util.Objects;
import processor.HttpRequestExecutor;
import vo.HttpRequestReader;
import vo.HttpResponseSender;
import vo.RequestResult;


public class RequestExecuteFilter implements Filter {
    private final HttpRequestExecutor httpRequestExecutor;

    public RequestExecuteFilter(HttpRequestExecutor httpRequestExecutor) {
        Objects.requireNonNull(httpRequestExecutor);
        this.httpRequestExecutor = httpRequestExecutor;
    }

    @Override
    public void doChain(HttpRequestReader httpRequestReader, HttpResponseSender httpResponseSender, FilterChain chain) {
        Objects.requireNonNull(httpRequestReader);
        Objects.requireNonNull(httpResponseSender);
        Objects.requireNonNull(chain);

        RequestResult result = httpRequestExecutor.execute(httpRequestReader, httpResponseSender);

        httpResponseSender.send(result);

        chain.doChain(httpRequestReader, httpResponseSender);
    }
}
