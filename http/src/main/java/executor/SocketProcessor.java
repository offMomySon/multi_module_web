package executor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
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
public class SocketProcessor {
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ServerSocket serverSocket;
    private final RequestRunner requestRunner;

    public SocketProcessor(ThreadPoolExecutor threadPoolExecutor, ServerSocket serverSocket, RequestRunner requestRunner) {
        Objects.requireNonNull(threadPoolExecutor);
        Objects.requireNonNull(serverSocket);
        Objects.requireNonNull(requestRunner);
        this.threadPoolExecutor = threadPoolExecutor;
        this.serverSocket = serverSocket;
        this.requestRunner = requestRunner;
    }

    public static SocketProcessor create(int poolSize, long keepAliceTime, int waitCapacity, int port, RequestRunner requestRunner) {
        Objects.requireNonNull(requestRunner);
        try {
            BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(waitCapacity);
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize, keepAliceTime, TimeUnit.MILLISECONDS, blockingQueue);
            ServerSocket socket = new ServerSocket(port);

            return new SocketProcessor(threadPoolExecutor, socket, requestRunner);
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to active server. Reason : `{0}`", e.getCause()), e);
        }
    }

    public void process() {
        log.info("start server.");

        while (true) {
            try {
                Socket socket = acceptSocket();

                Runnable task = () -> {
                    try (InputStream inputStream = socket.getInputStream();
                         OutputStream outputStream = socket.getOutputStream()) {
                        requestRunner.run(inputStream, outputStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                log.info("load task to thread.");
                threadPoolExecutor.execute(task);
            } catch (IOException e) {
                throw new RuntimeException(MessageFormat.format("I/O fail. Reason : `{0}`", e.getCause()));
            }
        }
    }

    private Socket acceptSocket() throws IOException {
        log.info("Ready client connection..");
        Socket socket = serverSocket.accept();
        log.info("socker connected.");
        return socket;
    }
}
