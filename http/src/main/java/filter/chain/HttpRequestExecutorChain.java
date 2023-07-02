package filter.chain;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import processor.HttpRequestProcessor;
import vo.HttpRequest;
import vo.HttpResponse;


@Slf4j
public class HttpRequestExecutorChain implements FilterChain {
    private final FilterChain nextFilterChain;
    private final HttpRequestProcessor httpRequestExecutor;

    public HttpRequestExecutorChain(HttpRequestProcessor httpRequestExecutor, FilterChain nextFilterChain) {
        Objects.requireNonNull(httpRequestExecutor);
        this.httpRequestExecutor = httpRequestExecutor;
        this.nextFilterChain = nextFilterChain;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        boolean execute = httpRequestExecutor.execute(request, response);
        if (execute) {
            log.info("request executed.");
            return;
        }
        log.info("does not request executed.");

        if (Objects.isNull(nextFilterChain)) {
            log.info("does not exist next filter chain.");
            return;
        }

        nextFilterChain.execute(request, response);
    }
}
