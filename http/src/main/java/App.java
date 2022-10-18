import config.IpAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import processor.Executor;
import request.HttpRequest;
import request.ServletRequest;
import request.ServletResponse;


@Slf4j
public class App {
    public static void main(String[] args) {
        ClientAccepter clientAccepter = new ClientAccepter();
        Executor executor = new Executor();

        while (true) {
            Socket socket = clientAccepter.accept();

            executor.execute(socket, socketRunnableConverter());
        }
    }

    public static Function<Socket, Runnable> socketRunnableConverter() {
        return (socket) -> socketRunnableCreator(socket);
    }

    private static Runnable socketRunnableCreator(Socket socket) {
        return () -> {
            try (socket) {
                ServletRequest servletRequest = ServletRequest.from(socket.getInputStream(), (InetSocketAddress) socket.getRemoteSocketAddress());
                ServletResponse servletResponse = ServletResponse.from(socket.getOutputStream());

                IpAddress remoteAddress = servletRequest.getRemoteAddress();
                HttpRequest httpRequest = servletRequest.getHttpRequest();


                Thread.sleep(6000);

                log.info("remoteAddress : `{}`", remoteAddress);
                log.info("http request : `{}`", httpRequest);

                servletResponse.sendResponse("test");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
