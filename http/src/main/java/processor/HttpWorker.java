package processor;

import filter.ApplicationFilterChain;
import filter.ApplicationFilterChainCreator;
import java.io.IOException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import vo.HttpRequest;
import vo.HttpRequestReader;
import vo.HttpResponse;

@Slf4j
public class HttpWorker implements Runnable {
    private final HttpRequestReader requestReader;
    private final HttpResponse response;
    private final ApplicationFilterChainCreator applicationFilterChainCreator;

    public HttpWorker(HttpRequestReader requestReader, HttpResponse response, ApplicationFilterChainCreator applicationFilterChainCreator) {
        Objects.requireNonNull(requestReader);
        Objects.requireNonNull(response);
        Objects.requireNonNull(applicationFilterChainCreator);

        this.requestReader = requestReader;
        this.response = response;
        this.applicationFilterChainCreator = applicationFilterChainCreator;
    }

    @Override
    public void run() {
        try (requestReader; response) {
            HttpRequest httpRequest = requestReader.read();

            ApplicationFilterChain applicationFilterChain = applicationFilterChainCreator.create(httpRequest.getHttpUri().getUrl());

            applicationFilterChain.doChain(httpRequest, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
