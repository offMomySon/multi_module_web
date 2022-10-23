package processor;

import java.io.IOException;
import response.Responser;

public class NotThreadExecutor implements Executor{
    @Override
    public void execute(Responser responser) {
        try {
            responser.send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
