package processor;

import config.Config;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import request.HttpRequest;
import response.HttpResponse;
import response.ResponseStatus;
import validate.ValidateUtil;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.*;

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
        log.info("start server.");

        try{
            while (true) {
                log.info("Ready client connection..");
                Socket socket = serverSocket.accept();

                log.info("load task.");
                threadPoolExecutor.execute(new Task(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("I/O fail. Reason : `{0}`", e.getCause()));
        }
    }

    private static class Task implements Runnable {
        private final Socket socket;

        public Task(Socket socket) {
            this.socket = validateNull(socket);
        }

        @Override
        public void run() {
            try(socket){
                HttpResponse httpResponse = new HttpResponse(socket.getOutputStream());

                ByteArrayInputStream inputStream = new ByteArrayInputStream("test body message".getBytes(UTF_8));

                httpResponse.header(ResponseStatus.OK.getStatusLine())
                    .body(inputStream)
                    .send();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
