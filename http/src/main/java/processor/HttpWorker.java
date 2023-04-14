package processor;

import java.io.IOException;
import java.util.Objects;
import vo.HttpRequest;
import vo.HttpRequestReader;
import vo.RequestResult;
import vo.ResponseSender;

public class HttpWorker implements Runnable {
    private final HttpRequestReader requestReader;
    private final ResponseSender responseSender;
    private final HttpRequestExecutor httpRequestExecutor;

    public HttpWorker(HttpRequestReader requestReader, ResponseSender responseSender, HttpRequestExecutor httpRequestExecutor) {
        Objects.requireNonNull(requestReader);
        Objects.requireNonNull(responseSender);
        Objects.requireNonNull(httpRequestExecutor);

        this.requestReader = requestReader;
        this.responseSender = responseSender;
        this.httpRequestExecutor = httpRequestExecutor;
    }

    @Override
    public void run() {
        try (requestReader; responseSender) {
            HttpRequest httpRequest = requestReader.read();

            RequestResult result = httpRequestExecutor.execute(httpRequest);

            responseSender.send(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
