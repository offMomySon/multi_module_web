package processor;

import config.Config;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/***
 * 역할.
 * http request 를 지속적으로 수신하고 thread 에 worker 를 할당함으로써 서비스를 수행하는 역할.
 */
@Slf4j
public class HttpService {
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ServerSocket serverSocket;
    private final HttpRequestExecutor httpRequestExecutor;

    public HttpService(HttpRequestExecutor httpRequestExecutor) {
        Objects.requireNonNull(httpRequestExecutor);

        try {
            this.threadPoolExecutor = new ThreadPoolExecutor(Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getKeepAliveTime(),
                                                             TimeUnit.MILLISECONDS,
                                                             new LinkedBlockingQueue<>(Config.INSTANCE.getWaitConnection()));
            this.serverSocket = new ServerSocket(Config.INSTANCE.getPort());
            this.httpRequestExecutor = httpRequestExecutor;
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to active server. Reason : `{0}`", e.getCause()), e);
        }
    }

    public void start() {
        log.info("start server.");

        while (true) {
            try {
                log.info("Ready client connection..");
                Socket socket = serverSocket.accept();

                log.info("load worker to thread.");
                threadPoolExecutor.execute(HttpWorker.create(socket.getInputStream(), socket.getOutputStream()));
            } catch (IOException e) {
                throw new RuntimeException(MessageFormat.format("I/O fail. Reason : `{0}`", e.getCause()));
            }
        }
    }
}
