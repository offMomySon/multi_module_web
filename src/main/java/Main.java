import config.Config;
import config.HttpConfig;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Accepter ACCEPTER = new Accepter(HttpConfig.instance.getPort());
    private static final ThreadPoolExecutor threadPoolExecutor =
        new ThreadPoolExecutor(Config.INSTANCE.getMaxConnection(), Config.INSTANCE.getMaxConnection(), Config.INSTANCE.getKeepAliveTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(Config.INSTANCE.getWaitConnection()));

    public static void main(String[] args) {
        while (true) {
            Socket accept = ACCEPTER.waitAccept();


            threadPoolExecutor.execute(()-> {
                System.out.println("test");
            });

        }
    }
}
