package processor;

import config.Config;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import request.HttpRequest;

/***
 * 역할.
 * 수신한 http request 를 적절한 로직을 차례대로 진행하고 http response 를 돌려준다.
 */
@Slf4j
public class HttpService {
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ServerSocket serverSocket;

    public HttpService() {
        try {
            this.threadPoolExecutor = new ThreadPoolExecutor(Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getKeepAliveTime(),
                                                             TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(Config.INSTANCE.getWaitConnection()));
            this.serverSocket = new ServerSocket(Config.INSTANCE.getPort());
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to active server. Reason : `{0}`", e.getCause()), e);
        }
    }

    public Socket start() {
        log.info("ready client connection..");
        while (true) {
            doService();
        }
    }

    private void doService() {
        try (Socket socket = serverSocket.accept()) {

            HttpRequest httpRequest = HttpRequest.parse(socket.getInputStream());

        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("I/O fail. Reason : `{0}`", e.getCause()));
        }
    }
}
