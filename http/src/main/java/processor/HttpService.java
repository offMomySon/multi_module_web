package processor;

import config.Config;
import filter.FilterWorker;
import filter.Filters;
import filter.chain.FilterChain;
import filter.chain.FilterWorkerChain;
import filter.chain.HttpRequestProcessorChain;
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
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ServerSocket serverSocket;
    private final HttpRequestProcessor httpRequestProcessor;
    private final Filters filters;

    public HttpService(HttpRequestProcessor httpRequestProcessor, Filters filters) {
        Objects.requireNonNull(httpRequestProcessor);
        Objects.requireNonNull(filters);

        try {
            this.threadPoolExecutor = new ThreadPoolExecutor(Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getKeepAliveTime(),
                                                             TimeUnit.MILLISECONDS,
                                                             new LinkedBlockingQueue<>(Config.INSTANCE.getWaitConnection()));
            this.serverSocket = new ServerSocket(Config.INSTANCE.getPort());
            this.httpRequestProcessor = httpRequestProcessor;
            this.filters = filters;
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to active server. Reason : `{0}`", e.getCause()), e);
        }
    }

//    httpRequest, httpResponse 를 받는다.
//    httpRequest 로 부터 url 을 가져온다.
//    url 에 매칭되는 filterWorker 를 가져온다.
//    applicationExecutor 를

    public void start() {
        log.info("start server.");

        SocketHttpTaskExecutor socketHttpTaskExecutor = SocketHttpTaskExecutor.create(5, 8080);
        socketHttpTaskExecutor.execute(((httpRequest, httpResponse) -> {
            List<FilterWorker> filterWorkers = filters.findFilterWorkers(httpRequest.getHttpUri().getUrl());

            log.info("create filter chain");
            FilterChain applicationExecutorChain = new HttpRequestProcessorChain(httpRequestProcessor, null);
            FilterChain filterChain = filterWorkers.stream()
                .reduce(
                    applicationExecutorChain,
                    FilterWorkerChain::new,
                    (pw, pw2) -> null);

            log.info("execute filter chain");
            filterChain.execute(httpRequest, httpResponse);
        }));
        // inputstream 으로 부터 httpRequestReader 를 생성한다.
        // httpRequestReader 를 읽어 httpRequest 를 생성한다.
        // outputStream 으로 부터 httpResponse 를 생성한다.
        // filters 에서 http url 을 기준으로 filterWorker 들을 뽑아온다.
        // filterWorker 와 http 실행자를 filter chain 으로 엮는다.
        // 엮은 filter chain 을 실행한다.

    }
}
