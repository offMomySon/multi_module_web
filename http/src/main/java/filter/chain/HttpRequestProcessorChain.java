package filter.chain;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import processor.HttpRequestProcessor;
import vo.HttpRequest;
import vo.HttpResponse;


@Slf4j
public class HttpRequestProcessorChain implements FilterChain {
    private final FilterChain nextFilterChain;
    private final HttpRequestProcessor httpRequestExecutor;

    public HttpRequestProcessorChain(HttpRequestProcessor httpRequestExecutor, FilterChain nextFilterChain) {
        Objects.requireNonNull(httpRequestExecutor);
        this.httpRequestExecutor = httpRequestExecutor;
        this.nextFilterChain = nextFilterChain;
    }

    @Override
    public boolean execute(HttpRequest request, HttpResponse response) {
        boolean execute = httpRequestExecutor.execute(request, response);
        if (execute) {
            log.info("request executed.");
            return true;
        }
        log.info("does not request executed.");

        if (Objects.isNull(nextFilterChain)) {
            log.info("does not exist next filter chain.");
            return true;
        }

        return nextFilterChain.execute(request, response);
    }
}
