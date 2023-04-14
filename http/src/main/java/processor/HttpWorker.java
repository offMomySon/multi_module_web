package processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import vo.HttpHeader;
import vo.HttpMethod;
import vo.HttpRequest;
import vo.HttpRequestReader;
import vo.HttpUri;
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

            HttpMethod httpMethod = httpRequest.getHttpMethod();
            HttpUri httpUri = httpRequest.getHttpUri();
            HttpHeader httpHeader = httpRequest.getHttpHeader();
            InputStream requestStream = httpRequest.getRequestStream();

            RequestResult result = httpRequestExecutor.execute(httpMethod, httpUri, httpHeader, requestStream);

            responseSender.send(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
