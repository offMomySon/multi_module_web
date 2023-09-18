package task;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import task.endpoint.EndPointTask;

public class HttpEmptyEndPointTask implements HttpEndPointTask{
    private final EndPointTask endPointTask;

    public HttpEmptyEndPointTask(EndPointTask endPointTask) {
        Objects.requireNonNull(endPointTask);
        this.endPointTask = endPointTask;
    }

    @Override
    public Parameter[] getExecuteParameters() {
        return endPointTask.getExecuteParameters();
    }

    @Override
    public Optional<HttpTaskResult> execute(Object[] params) {
        Objects.requireNonNull(params);
        endPointTask.execute(params);
        return Optional.empty();
    }
}
