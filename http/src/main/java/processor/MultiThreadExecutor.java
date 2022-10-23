package processor;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import response.Responser;
import util.ValidateUtil;
import static util.ValidateUtil.*;

@Slf4j
public class MultiThreadExecutor implements Executor{
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
                                                                                 5,
                                                                                 500000,
                                                                                 TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10));

    private final Responser responser;

    public MultiThreadExecutor(Responser responser) {
        validateNull(responser);
        
        this.responser = responser;
    }

    public void execute(Responser responser) {

        threadPoolExecutor.execute(()-> {
            try {
                responser.send();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
