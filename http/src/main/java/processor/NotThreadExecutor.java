package processor;

import java.io.IOException;
import response.Responser;
import util.ValidateUtil;
import static util.ValidateUtil.*;

public class NotThreadExecutor implements Executor{
    private final Responser responser;

    public NotThreadExecutor(Responser responser) {
        validateNull(responser);

        this.responser = responser;
    }

    @Override
    public void execute() {
        try {
            responser.send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
