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
import processor.MultiThreadExecutor;
import processor.NotThreadExecutor;
import request.Uri;
import response.Responser;
import static java.nio.charset.StandardCharsets.UTF_8;


@Slf4j
public class App {
    private static final String HEADER_END_LINE = "\r\n\r\n";
    private static final String END_OF_LINE = "\r\n";
    private static final int BUFFER_SIZE = 8192;
    private static final int INCREASE_BUFFER_SIZE_FACTOR = 2;

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 500000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(10));


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

            Set<IpAddress> banIpAddresses = HttpConfig.instance.getBanIpAddresses();
            boolean isBanIpAddress = banIpAddresses.contains(remoteAddress);
            if (isBanIpAddress) {
                log.info("ban ip address.");

                String content = "Ban address.";
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes(UTF_8));

                Responser responser = Responser.build()
                    .contentType(Responser.ContentType.TEXT)
                    .status(Responser.Status.SERVICE_UNAVAILABLE)
                    .socketOutputStream(socket.getOutputStream())
                    .contentInputStream(byteArrayInputStream)
                    .build();

                NotThreadExecutor notThreadExecutor = new NotThreadExecutor();
                notThreadExecutor.execute(responser);
                return;
            }

            boolean doesNotLeftThread = threadPoolExecutor.getMaximumPoolSize() == threadPoolExecutor.getActiveCount() && threadPoolExecutor.getQueue().remainingCapacity() == 0;
            if (doesNotLeftThread) {
                String content = "Does not left Thread.";
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes(UTF_8));

                Responser responser = Responser.build()
                    .contentType(Responser.ContentType.TEXT)
                    .status(Responser.Status.SERVICE_UNAVAILABLE)
                    .socketOutputStream(socket.getOutputStream())
                    .contentInputStream(byteArrayInputStream)
                    .build();

                NotThreadExecutor notThreadExecutor = new NotThreadExecutor();
                notThreadExecutor.execute(responser);
                return;
            }

            BufferedInputStream contentInputStream = contentFinder.createInputStream(uri.getValue());

            Responser responser = Responser.build()
                .contentType(Responser.ContentType.TEXT)
                .status(Responser.Status.OK)
                .socketOutputStream(socket.getOutputStream())
                .contentInputStream(contentInputStream)
                .build();

            MultiThreadExecutor multiThreadExecutor = new MultiThreadExecutor(responser);
            multiThreadExecutor.execute(responser);
        }
    }
}
