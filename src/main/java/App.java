import accept.RequestAcceptor;
import config.Config;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class App {
    private static final RequestAcceptor REQUEST_ACCEPTER = new RequestAcceptor(Config.INSTANCE.getPort());
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Config.INSTANCE.getMaxConnection(), Config.INSTANCE.getMaxConnection(), Config.INSTANCE.getKeepAliveTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(Config.INSTANCE.getWaitConnection()));
    private static final ProtocolDecoder protocolDecoder = new ProtocolDecoder();

    public static void main(String[] args) {
        while (true) {
            Socket socket = REQUEST_ACCEPTER.waitAccept();

            try {
                protocolDecoder.decode(socket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            threadPoolExecutor.execute(()-> {
                System.out.println("test");
            });

        }
    }
}