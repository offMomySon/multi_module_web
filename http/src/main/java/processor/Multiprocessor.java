package processor;

import config.HttpConfig;
import config.IpAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import request.HttpRequest;
import request.ServletRequest;
import request.ServletResponse;
import static util.ValidateUtil.validateNull;

@Slf4j
public class Multiprocessor {
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(HttpConfig.instance.getMaxConnection(),
                                                                                        HttpConfig.instance.getMaxConnection(),
                                                                                        HttpConfig.instance.getKeepAliveTime(),
                                                                                        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(HttpConfig.instance.getWaitConnection()));
    private final Socket socket;

    public Multiprocessor(Socket socket) {
        // not socket -> need to convert some obj
        validateNull(socket);

        this.socket = socket;
    }

    public void process() {
        threadPoolExecutor.execute(doProcess(socket));
    }

    private static Runnable doProcess(Socket socket) {
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
