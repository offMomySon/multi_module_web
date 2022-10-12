import config.Config;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import request.HttpRequest;
import request.ServletRequest;
import request.ServletResponse;

@Slf4j
public class Server {
    private final ServerSocket serverSocket;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(8080);
            Config instance = Config.instance;
            String s = instance.toString();
            log.info("S : `{}`", s);
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to active server. Reason : `{0}`",e.getCause()), e);
        }
    }

    public void run() {
        log.info("start server. read to connection..");
        while (true) {
            try (Socket socket = serverSocket.accept();
                 ServletRequest servletRequest = ServletRequest.from(socket);
                 ServletResponse servletResponse = ServletResponse.from(socket)) {


                HttpRequest httpRequest = servletRequest.readHttpRequest();
                log.info("http reuqest : `{}`", httpRequest);

                servletResponse.sendResponse("test");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
