package processor;

import config.IpAddress;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import request.Uri;
import response.Responser;
import util.ValidateUtil;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class ExecutorCreator {
    private final Set<IpAddress> banIpAddresses;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final OutputStream outputStream;
    private final Uri uri;

    public ExecutorCreator(Set<IpAddress> banIpAddresses, ThreadPoolExecutor threadPoolExecutor, OutputStream outputStream, Uri uri) {
        ValidateUtil.validateNull(banIpAddresses);
        ValidateUtil.validateNull(threadPoolExecutor);
        ValidateUtil.validateNull(outputStream);
        ValidateUtil.validateNull(uri);

        this.banIpAddresses = banIpAddresses;
        this.threadPoolExecutor = threadPoolExecutor;
        this.outputStream = outputStream;
        this.uri = uri;
    }

    public Executor create(IpAddress remoteAddress) {
        boolean isBanIpAddress = banIpAddresses.contains(remoteAddress);
        if (isBanIpAddress) {
            log.info("ban ip address.");

            String content = "Ban address.";
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes(UTF_8));

            Responser responser = Responser.build()
                .contentType(Responser.ContentType.TEXT)
                .status(Responser.Status.SERVICE_UNAVAILABLE)
                .socketOutputStream(outputStream)
                .contentInputStream(byteArrayInputStream)
                .build();

            NotThreadExecutor notThreadExecutor = new NotThreadExecutor(responser);
            return notThreadExecutor;
        }

        boolean doesNotLeftThread = threadPoolExecutor.getMaximumPoolSize() == threadPoolExecutor.getActiveCount() && threadPoolExecutor.getQueue().remainingCapacity() == 0;
        if (doesNotLeftThread) {
            String content = "Does not left Thread.";
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes(UTF_8));

            Responser responser = Responser.build()
                .contentType(Responser.ContentType.TEXT)
                .status(Responser.Status.SERVICE_UNAVAILABLE)
                .socketOutputStream(outputStream)
                .contentInputStream(byteArrayInputStream)
                .build();

            NotThreadExecutor notThreadExecutor = new NotThreadExecutor(responser);
            return notThreadExecutor;
        }


        MultiThreadExecutor multiThreadExecutor = new MultiThreadExecutor(threadPoolExecutor, outputStream, uri);
        return multiThreadExecutor;
    }
}
