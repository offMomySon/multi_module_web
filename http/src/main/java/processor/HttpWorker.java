package processor;

import java.io.IOException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import vo.HttpRequestReader;
import vo.HttpResponseSender;
import vo.RequestResult;

@Slf4j
public class HttpWorker implements Runnable {
    private final HttpRequestReader requestReader;
    private final HttpResponseSender httpResponseSender;
    private final HttpRequestExecutor httpRequestExecutor;

    public HttpWorker(HttpRequestReader requestReader, HttpResponseSender httpResponseSender, HttpRequestExecutor httpRequestExecutor) {
        Objects.requireNonNull(requestReader);
        Objects.requireNonNull(httpResponseSender);
        Objects.requireNonNull(httpRequestExecutor);

        this.requestReader = requestReader;
        this.httpResponseSender = httpResponseSender;
        this.httpRequestExecutor = httpRequestExecutor;
    }

    @Override
    public void run() {
        try (requestReader; httpResponseSender) {
            RequestResult result = httpRequestExecutor.execute(requestReader, httpResponseSender);

            httpResponseSender.send(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
