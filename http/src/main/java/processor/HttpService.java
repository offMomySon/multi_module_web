package processor;

import config.Config;
import filter.FilterWorker;
import filter.Filters;
import filter.chain.BaseFilterWorkerChain;
import filter.chain.FilterWorkerChain;
import filter.chain.HttpRequestExecutorChain;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.List;
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
    private final HttpRequestExecutor httpRequestExecutor;
    private final HttpRequestExecutor httpStaticResourceExecutor;
    private final Filters filters;

    public HttpService(HttpRequestExecutor httpRequestExecutor, HttpRequestExecutor httpStaticResourceExecutor, Filters filters) {
        Objects.requireNonNull(httpRequestExecutor);
        Objects.requireNonNull(httpStaticResourceExecutor);
        Objects.requireNonNull(filters);

        try {
            this.threadPoolExecutor = new ThreadPoolExecutor(Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getKeepAliveTime(),
                                                             TimeUnit.MILLISECONDS,
                                                             new LinkedBlockingQueue<>(Config.INSTANCE.getWaitConnection()));
            this.serverSocket = new ServerSocket(Config.INSTANCE.getPort());
            this.httpRequestExecutor = httpRequestExecutor;
            this.httpStaticResourceExecutor = httpStaticResourceExecutor;
            this.filters = filters;
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

                Runnable workerTask = createWorkerTask(inputStream, outputStream);

                log.info("load task to thread.");
                threadPoolExecutor.execute(workerTask);
            } catch (IOException e) {
                throw new RuntimeException(MessageFormat.format("I/O fail. Reason : `{0}`", e.getCause()));
            }
        }
    }

    private Runnable createWorkerTask(InputStream inputStream, OutputStream outputStream) {
        log.info("create worker task.");

        return () -> {
            log.info("start task");

            try (HttpRequestReader httpRequestReader = new HttpRequestReader(inputStream);
                 HttpResponse httpResponse = new HttpResponse(outputStream)) {
                HttpRequest httpRequest = httpRequestReader.read();

                List<FilterWorker> filterWorkers = filters.findFilterWorkers(httpRequest.getHttpUri().getUrl());

                log.info("create filter chain");
                FilterWorkerChain lastFilterWorkerChain = new HttpRequestExecutorChain(httpStaticResourceExecutor, httpRequestExecutor);
                FilterWorkerChain filterWorkerChain = filterWorkers.stream()
                    .reduce(
                        lastFilterWorkerChain,
                        BaseFilterWorkerChain::new,
                        (pw, pw2) -> null);

                log.info("execute filter chain");
                filterWorkerChain.execute(httpRequest, httpResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private Socket acceptSocket() throws IOException {
        log.info("Ready client connection..");
        Socket socket = serverSocket.accept();
        log.info("socker connected.");
        return socket;
    }
}
