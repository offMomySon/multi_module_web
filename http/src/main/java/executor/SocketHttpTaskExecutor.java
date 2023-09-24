package executor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import vo.HttpRequest;
import vo.HttpRequestReader;
import vo.HttpResponse;

public class SocketHttpTaskExecutor {
    private final ExecutorService executorService;
    private final ServerSocket serverSocket;

    public SocketHttpTaskExecutor(ExecutorService executorService, ServerSocket serverSocket) {
        this.executorService = executorService;
        this.serverSocket = serverSocket;
    }

    public static SocketHttpTaskExecutor create(int threadPoolCount, int port) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolCount);
        ServerSocket serverSocket = createServerSocket(port);
        return new SocketHttpTaskExecutor(executorService, serverSocket);
    }

    public static SocketHttpTaskExecutor create(int port, int maxConnection, int waitConnection, long keepAliveTime){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(maxConnection,
                                                                       maxConnection,
                                                                       keepAliveTime,
                                                                       TimeUnit.MILLISECONDS,
                                                                       new LinkedBlockingQueue<>(waitConnection));
        ServerSocket serverSocket = createServerSocket(port);
        return new SocketHttpTaskExecutor(threadPoolExecutor, serverSocket);
    }

    private static ServerSocket createServerSocket(int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(BiConsumer<HttpRequest, HttpResponse> consumer) {
        while (true) {
            Socket socket = accept();

            Runnable runnable = () -> {
                InputStream inputStream = getInputStream(socket);
                OutputStream outputStream = getOutputStream(socket);

                try (HttpRequestReader httpRequestReader = new HttpRequestReader(inputStream);
                     HttpResponse httpResponse = new HttpResponse(outputStream)) {
                    HttpRequest httpRequest = httpRequestReader.read();

                    consumer.accept(httpRequest, httpResponse);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            executorService.execute(runnable);
        }
    }

    private static OutputStream getOutputStream(Socket socket) {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getInputStream(Socket socket) {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Socket accept() {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
