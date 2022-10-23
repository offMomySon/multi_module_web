import config.HttpConfig;
import config.IpAddress;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import processor.Executor;
import processor.ExecutorCreator;
import processor.MultiThreadExecutor;
import processor.NotThreadExecutor;
import request.Uri;
import response.Responser;
import util.ContentFinder;
import static java.nio.charset.StandardCharsets.UTF_8;


@Slf4j
public class App {
    private static final String HEADER_END_LINE = "\r\n\r\n";
    private static final String END_OF_LINE = "\r\n";
    private static final int BUFFER_SIZE = 8192;
    private static final int INCREASE_BUFFER_SIZE_FACTOR = 2;

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 500000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10));

    public static void main(String[] args) throws IOException {
        ClientAccepter clientAccepter = new ClientAccepter();
        ContentFinder contentFinder = new ContentFinder();

        // 파싱하는 역할.
        // 파싱하고 데이터를 어떻게 전달한거냐 역할.
        // 데이터 전송 역할.
        while (true) {
            Socket socket = clientAccepter.accept();
            RequestParser requestParser = RequestParser.parse(socket.getInputStream(), (InetSocketAddress) socket.getRemoteSocketAddress());

            Uri uri = requestParser.getUri();
            IpAddress remoteAddress = requestParser.getRemoteAddress();

            ExecutorCreator executorCreator = new ExecutorCreator(HttpConfig.instance.getBanIpAddresses(), threadPoolExecutor, socket.getOutputStream(), uri);
            Executor executor = executorCreator.create(remoteAddress);

            executor.execute();
        }
    }
}
