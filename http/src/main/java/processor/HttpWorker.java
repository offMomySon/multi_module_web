package processor;

import java.io.IOException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import vo.HttpRequestReader;
import vo.HttpResponseWriter;

@Slf4j
public class HttpWorker implements Runnable {
    private final HttpRequestReader requestReader;
    private final HttpResponseWriter httpResponseWriter;
    private final HttpRequestExecutor httpRequestExecutor;

    public HttpWorker(HttpRequestReader requestReader, HttpResponseWriter httpResponseWriter, HttpRequestExecutor httpRequestExecutor) {
        Objects.requireNonNull(requestReader);
        Objects.requireNonNull(httpResponseWriter);
        Objects.requireNonNull(httpRequestExecutor);

        this.requestReader = requestReader;
        this.httpResponseWriter = httpResponseWriter;
        this.httpRequestExecutor = httpRequestExecutor;
    }

    @Override
    public void run() {
        try (requestReader; httpResponseWriter) {
//            RequestResult result = httpRequestExecutor.execute(requestReader, httpResponseSender);
//            httpResponseSender.send(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
