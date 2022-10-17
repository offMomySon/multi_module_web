import config.HttpConfig;
import config.IpAddress;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import request.HttpRequest;
import request.ServletRequest;
import request.ServletResponse;
import static util.ValidateUtil.validateNull;

@Slf4j
public class ClientAccepter {
    private final ServerSocket serverSocket;

    public ClientAccepter() {
        try {
            this.serverSocket = new ServerSocket(HttpConfig.instance.getPort());
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to active server. Reason : `{0}`", e.getCause()), e);
        }
    }

    public Socket accept() {
        log.info("start server. ready client connection..");
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                return socket;
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
}
