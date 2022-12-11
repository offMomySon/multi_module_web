import processor.RequestProcessor;
import config.Config;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class App {
    private static final RequestProcessor REQUEST_ACCEPTER = new RequestProcessor(Config.INSTANCE.getPort());
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Config.INSTANCE.getMaxConnection(), Config.INSTANCE.getMaxConnection(), Config.INSTANCE.getKeepAliveTime(),
                                                                                        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(Config.INSTANCE.getWaitConnection()));

    public static void main(String[] args) {
    }
}