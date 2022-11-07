package processor;

import config.IpAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import response.Responser;
import util.ValidateUtil;

@Slf4j
public class ThreadExecutor {
    private final ThreadPoolExecutor threadPoolExecutor;

    public ThreadExecutor(ThreadPoolExecutor threadPoolExecutor) {
        ValidateUtil.validateNull(threadPoolExecutor);

        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void execute(Runnable executeRunnable, Runnable rejectRunnable) {
        try{
            threadPoolExecutor.execute(executeRunnable);
        } catch (RejectedExecutionException rejectedExecutionException){
            rejectRunnable.run();
        }

    }
}
