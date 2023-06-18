package processor;

import config.Config;
import filter.FilterWorker;
import filter.Filters;
import filter.chain.FilterChain;
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
    private final HttpRequestExecutor applicationExecutor;
    private final HttpRequestExecutor staticResourceExecutor;
    private final Filters filters;

    public HttpService(HttpRequestExecutor applicationExecutor, HttpRequestExecutor staticResourceExecutor, Filters filters) {
        Objects.requireNonNull(applicationExecutor);
        Objects.requireNonNull(staticResourceExecutor);
        Objects.requireNonNull(filters);

        try {
            this.threadPoolExecutor = new ThreadPoolExecutor(Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getKeepAliveTime(),
                                                             TimeUnit.MILLISECONDS,
                                                             new LinkedBlockingQueue<>(Config.INSTANCE.getWaitConnection()));
            this.serverSocket = new ServerSocket(Config.INSTANCE.getPort());

            this.applicationExecutor = applicationExecutor;
            this.staticResourceExecutor = staticResourceExecutor;
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

                // todo. - notion .
                log.info("create filter chain");
                FilterChain applicationExecutorChain = new HttpRequestExecutorChain(applicationExecutor, null);
                FilterChain staticResourceExecutorChain = new HttpRequestExecutorChain(staticResourceExecutor, applicationExecutorChain);
                FilterChain filterChain = filterWorkers.stream()
                    .reduce(
                        staticResourceExecutorChain,
                        FilterWorkerChain::new,
                        (pw, pw2) -> null);

                log.info("execute filter chain");
                filterChain.execute(httpRequest, httpResponse);
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
