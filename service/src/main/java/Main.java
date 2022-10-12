import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorService executorService2 = Executors.newFixedThreadPool(1000);

        AtomicInteger atomicInteger = new AtomicInteger();

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService2;

        int max = 100_000;

        for (int i = 0; i < max; i++) {
            executorService2.execute(()-> {
                sleep(Duration.ofMillis(1000));
                atomicInteger.incrementAndGet();
            });

            System.out.println(threadPoolExecutor.getActiveCount());
        }


        System.out.println("Thread pool.");
        while(max > atomicInteger.get()){
            sleep(Duration.ofMillis(500));
            System.out.println(atomicInteger.get());
        }
        System.out.println("done.");
    }


    private static void sleep(Duration duration){
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

