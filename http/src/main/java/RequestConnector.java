import config.HttpConfig;
import config.IpAddress;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import request.HttpRequest;
import request.ServletRequest;
import request.ServletResponse;
import static util.ValidateUtil.validateNull;

@Slf4j
public class RequestConnector {
    private final ServerSocket serverSocket;

    public RequestConnector() {
        try {
            this.serverSocket = new ServerSocket(HttpConfig.instance.getPort());
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to active server. Reason : `{0}`", e.getCause()), e);
        }
    }

    public void run() {
        log.info("start server. read to connection..");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(HttpConfig.instance.getMaxConnection(),
                                                                       HttpConfig.instance.getMaxConnection(),
                                                                       HttpConfig.instance.getKeepAliveTime(),
                                                                       TimeUnit.MILLISECONDS,
                                                                       new LinkedBlockingQueue<>(HttpConfig.instance.getWaitConnection()));

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                threadPoolExecutor.execute(doServlet(socket));
            } catch (Exception e) {
                validateNull(socket);

                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        }
    }

    private static Runnable doServlet(Socket socket) {
        return () -> {
            try (socket) {
                ServletRequest servletRequest = ServletRequest.from(socket.getInputStream(), (InetSocketAddress) socket.getRemoteSocketAddress());
                ServletResponse servletResponse = ServletResponse.from(socket.getOutputStream());

                IpAddress remoteAddress = servletRequest.getRemoteAddress();
                HttpRequest httpRequest = servletRequest.getHttpRequest();


                log.info("remoteAddress : `{}`", remoteAddress);
                log.info("http request : `{}`", httpRequest);

                servletResponse.sendResponse("test");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

}
