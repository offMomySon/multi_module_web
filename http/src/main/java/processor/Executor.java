package processor;

import config.HttpConfig;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import util.ValidateUtil;
import static util.ValidateUtil.validateNull;

@Slf4j
public class Executor {
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(HttpConfig.instance.getMaxConnection(),
                                                                                 HttpConfig.instance.getMaxConnection(),
                                                                                 HttpConfig.instance.getKeepAliveTime(),
                                                                                 TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(HttpConfig.instance.getWaitConnection()));

    public void execute(Socket socket, Function<Socket, Runnable> runnableFunction) {
        Runnable runnable = runnableFunction.apply(socket);

        try {
            threadPoolExecutor.execute(runnable);
        } catch (RejectedExecutionException e) {
            try {
                validateNull(socket);

                log.info("task aborted.. try close socket.");
                socket.close();
            } catch (IOException ex) {
                log.info("socket close fail.. ");
                throw new RuntimeException(ex);
            }
        }
    }
}
