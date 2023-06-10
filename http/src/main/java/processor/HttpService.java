package processor;

import config.Config;
import filter.ApplicationFilterChain;
import filter.ApplicationFilterChainCreator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import vo.HttpRequest;
import vo.HttpRequestReader;
import vo.HttpResponse;

/***
 * 역할.
 * http request 를 지속적으로 수신하고 thread 에 worker 를 할당함으로써 서비스를 수행하는 역할.
 */
@Slf4j
public class HttpService {
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ServerSocket serverSocket;
    private final ApplicationFilterChainCreator applicationFilterChainCreator;

    public HttpService(ApplicationFilterChainCreator applicationFilterChainCreator) {
        Objects.requireNonNull(applicationFilterChainCreator);

        try {
            this.threadPoolExecutor = new ThreadPoolExecutor(Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getKeepAliveTime(),
                                                             TimeUnit.MILLISECONDS,
                                                             new LinkedBlockingQueue<>(Config.INSTANCE.getWaitConnection()));
            this.serverSocket = new ServerSocket(Config.INSTANCE.getPort());
            this.applicationFilterChainCreator = applicationFilterChainCreator;
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to active server. Reason : `{0}`", e.getCause()), e);
        }
    }

    public void start() {
        log.info("start server.");

        while (true) {
            try {
                Socket socket = acceptSocket();
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                HttpWorker httpWorker = createHttpWorker(inputStream, outputStream, applicationFilterChainCreator);

                log.info("load request to thread.");
                threadPoolExecutor.execute(createWorkerTask(inputStream, outputStream));
            } catch (IOException e) {
                throw new RuntimeException(MessageFormat.format("I/O fail. Reason : `{0}`", e.getCause()));
            }
        }
    }

    private Runnable createWorkerTask(InputStream inputStream, OutputStream outputStream) {
        return () -> {
            log.info("start to create requestWorker");

            try (HttpRequestReader httpRequestReader = new HttpRequestReader(inputStream);
                 HttpResponse httpResponse = new HttpResponse(outputStream)) {

                HttpRequest httpRequest = httpRequestReader.read();
                ApplicationFilterChain applicationFilterChain = applicationFilterChainCreator.create(httpRequest.getHttpUri().getUrl());

                applicationFilterChain.doChain(httpRequest, httpResponse);
//                HttpWorker httpWorker = createHttpWorker(httpRequestReader, httpResponse, applicationFilterChainCreator);
//                httpWorker.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private static HttpWorker createHttpWorker(HttpRequestReader httpRequestReader, HttpResponse httpResponse, ApplicationFilterChainCreator applicationFilterChainCreator) {
        log.info("start to create requestWorker");
        HttpWorker httpWorker = new HttpWorker(httpRequestReader, httpResponse, applicationFilterChainCreator);
        log.info("created requestWorker");
        return httpWorker;
    }

    private static HttpWorker createHttpWorker(InputStream inputStream, OutputStream outputStream, ApplicationFilterChainCreator applicationFilterChainCreator) {
        log.info("start to create requestWorker");
        HttpRequestReader httpRequestReader = new HttpRequestReader(inputStream);
        HttpResponse httpResponse = new HttpResponse(outputStream);
        HttpWorker httpWorker = new HttpWorker(httpRequestReader, httpResponse, applicationFilterChainCreator);
        log.info("created requestWorker");
        return httpWorker;
    }

    private Socket acceptSocket() throws IOException {
        log.info("Ready client connection..");
        Socket socket = serverSocket.accept();
        log.info("socker connected.");
        return socket;
    }
}
