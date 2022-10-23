import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(10);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 60000, TimeUnit.MILLISECONDS, linkedBlockingQueue);

        threadPoolExecutor.execute(()-> sleep(Duration.ofSeconds(10)));
        threadPoolExecutor.execute(()-> sleep(Duration.ofSeconds(10)));
        threadPoolExecutor.execute(()-> sleep(Duration.ofSeconds(10)));
        threadPoolExecutor.execute(()-> sleep(Duration.ofSeconds(10)));

        System.out.println(linkedBlockingQueue.remainingCapacity());




    }


    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

