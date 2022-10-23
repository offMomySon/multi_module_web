import config.HttpConfig;
import config.IpAddress;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import processor.Executor;
import request.Uri;
import static java.nio.charset.StandardCharsets.UTF_8;
import static util.IoUtils.createBufferedOutputStream;


@Slf4j
public class App {
    private static final String HEADER_END_LINE = "\r\n\r\n";
    private static final String END_OF_LINE = "\r\n";
    private static final int BUFFER_SIZE = 8192;
    private static final int INCREASE_BUFFER_SIZE_FACTOR = 2;

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 500000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(10));


    public static void main(String[] args) throws IOException {
        ClientAccepter clientAccepter = new ClientAccepter();
        Executor executor = new Executor();
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
                StringBuilder responseBuilder = new StringBuilder();
                responseBuilder.append("HTTP/1.1 403 Forbidden").append(END_OF_LINE);
                responseBuilder.append("Content-Length : ").append(content.length()).append(END_OF_LINE);
                responseBuilder.append("Content-Type: ").append("text/html").append(END_OF_LINE);
                responseBuilder.append("Date: ").append(new Date()).append(END_OF_LINE);
                responseBuilder.append(END_OF_LINE);
                responseBuilder.append(content).append(END_OF_LINE);
                String response = responseBuilder.toString();

                BufferedOutputStream bufferedOutputStream = createBufferedOutputStream(socket.getOutputStream());
                bufferedOutputStream.write(response.getBytes(StandardCharsets.UTF_8));
                bufferedOutputStream.flush();
                return;
            }

            boolean doesNotLeftThread = threadPoolExecutor.getMaximumPoolSize() == threadPoolExecutor.getActiveCount() && threadPoolExecutor.getQueue().remainingCapacity() == 0;
            if (doesNotLeftThread) {

                String content = "Does not left Thread.";
                StringBuilder responseBuilder = new StringBuilder();
                responseBuilder.append("HTTP/1.1 503 Service Unavailable").append(END_OF_LINE);
                responseBuilder.append("Content-Length : ").append(content.length()).append(END_OF_LINE);
                responseBuilder.append("Content-Type: ").append("text/html").append(END_OF_LINE);
                responseBuilder.append("Date: ").append(new Date()).append(END_OF_LINE);
                responseBuilder.append(END_OF_LINE);
                responseBuilder.append(content).append(END_OF_LINE);
                String response = responseBuilder.toString();

                BufferedOutputStream bufferedOutputStream = createBufferedOutputStream(socket.getOutputStream());
                bufferedOutputStream.write(response.getBytes(StandardCharsets.UTF_8));
                bufferedOutputStream.flush();
                return;
            }



            threadPoolExecutor.execute(()-> {
                try {
                    sendContentResponse(socket, contentFinder, uri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void sendContentResponse(Socket socket, ContentFinder contentFinder, Uri uri) throws IOException {
        // how to send dto to another module.
        BufferedInputStream contentInputStream = contentFinder.createInputStream(uri.getValue());

        byte[] BUFFER = new byte[BUFFER_SIZE];
        byte[] readBytes = new byte[BUFFER_SIZE];

        int nextIndex = 0;
        while (doesNotEndOfStream(contentInputStream)) {
            int readLength = contentInputStream.read(BUFFER, 0, BUFFER_SIZE);

            boolean needIncreaseBuffer = readBytes.length <= nextIndex + readLength;
            if (needIncreaseBuffer) {
                byte[] newReadBytes = Arrays.copyOf(readBytes, readBytes.length * INCREASE_BUFFER_SIZE_FACTOR);
                readBytes = newReadBytes;
            }

            System.arraycopy(BUFFER, 0, readBytes, nextIndex, readLength);
            nextIndex += readLength;
        }
        String content = new String(readBytes, 0, nextIndex, UTF_8);

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 200 OK").append(END_OF_LINE);
        responseBuilder.append("Content-Length : ").append(content.length()).append(END_OF_LINE);
        responseBuilder.append("Content-Type: ").append("text/html").append(END_OF_LINE);
        responseBuilder.append("Date: ").append(new Date()).append(END_OF_LINE);
        responseBuilder.append(END_OF_LINE);
        responseBuilder.append(content).append(END_OF_LINE);
        String response = responseBuilder.toString();

        BufferedOutputStream bufferedOutputStream = createBufferedOutputStream(socket.getOutputStream());
        bufferedOutputStream.write(response.getBytes(StandardCharsets.UTF_8));
        bufferedOutputStream.flush();
    }

    private static boolean doesNotEndOfStream(InputStream inputStream) throws IOException {
        return inputStream.available() != 0;
    }
}
