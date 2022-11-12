import config.HttpConfig;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Accepter ACCEPTER = new Accepter(HttpConfig.instance.getPort());
    private static final ThreadPoolExecutor threadPoolExecutor =
        new ThreadPoolExecutor(5, 5, 500000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10));

    public static void main(String[] args) {
        while(true){
            Socket accept = ACCEPTER.waitAccept();


        }
    }
}
