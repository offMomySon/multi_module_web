package processor;

import filter.FilterWorker;
import filter.Filters;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

// todo
// processor vs service 가 어느때 사용되어야 적절한가?
//
// processor
//
@Slf4j
public class HttpService {
    private final HttpRequestProcessor httpRequestProcessor;
    private final Filters filters;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ServerSocket serverSocket;

    public HttpService(HttpRequestProcessor httpRequestProcessor, Filters filters, ThreadPoolExecutor threadPoolExecutor, ServerSocket serverSocket) {
        Objects.requireNonNull(httpRequestProcessor);
        Objects.requireNonNull(filters);
        Objects.requireNonNull(threadPoolExecutor);
        Objects.requireNonNull(serverSocket);
        this.httpRequestProcessor = httpRequestProcessor;
        this.filters = filters;
        this.threadPoolExecutor = threadPoolExecutor;
        this.serverSocket = serverSocket;
    }

    public static HttpService from(HttpRequestProcessor httpRequestProcessor, Filters filters,
                                   int port, int maxConnection, int waitConnection, long keepAliveTime) {
        Objects.requireNonNull(httpRequestProcessor);
        Objects.requireNonNull(filters);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(maxConnection,
                                                                       maxConnection,
                                                                       keepAliveTime,
                                                                       TimeUnit.MILLISECONDS,
                                                                       new LinkedBlockingQueue<>(waitConnection));
        ServerSocket serverSocket = createServerSocket(port);
        return new HttpService(httpRequestProcessor, filters, threadPoolExecutor, serverSocket);
    }

    private static ServerSocket createServerSocket(int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to create server socket. Reason : `{0}`", e.getCause()), e);
        }
    }

    //    httpRequest, httpResponse 를 받는다.
//    httpRequest 로 부터 url 을 가져온다.
//    url 에 매칭되는 filterWorker 를 가져온다.
//    httpRequestProcessor 를 filterChain 에 등록한다.
//    filterWorker 들을 filterChain 에 차례대로 등록한다.
//    filterChain 을 실행시킨다.
    public void start() {
        log.info("start server.");

        SocketHttpTaskExecutor socketHttpTaskExecutor = new SocketHttpTaskExecutor(threadPoolExecutor, serverSocket);
        socketHttpTaskExecutor.execute(((httpRequest, httpResponse) -> {
            List<FilterWorker> filterWorkers = filters.findFilterWorkers(httpRequest.getHttpRequestPath().getValue().toString());

            for (FilterWorker filterWorker : filterWorkers) {
                filterWorker.prevExecute(httpRequest, httpResponse);
            }

            httpRequestProcessor.execute(httpRequest, httpResponse);

            for (FilterWorker filterWorker : filterWorkers) {
                filterWorker.postExecute(httpRequest, httpResponse);
            }
        }));
    }
}
