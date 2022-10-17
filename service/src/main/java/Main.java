import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        Thread t1 = new Thread(()-> {
            try {
                System.out.println("t1 waited.");
                Socket accept = serverSocket.accept();
                System.out.println("t1 accepted.");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        Thread t2 = new Thread(()-> {
            try {
                System.out.println("t2 waited.");
                Socket accept = serverSocket.accept();
                System.out.println("t2 accepted.");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("done.");
    }


    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

