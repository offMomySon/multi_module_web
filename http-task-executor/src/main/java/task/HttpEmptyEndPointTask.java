package task;

import java.util.Objects;
import java.util.Optional;
import parameter.matcher.ParameterAndValueAssigneeType;
import task.worker.EndPointTaskWorker;

public class HttpEmptyEndPointTask implements HttpEndPointTask{
    private final EndPointTaskWorker endPointTaskWorker;

    public HttpEmptyEndPointTask(EndPointTaskWorker endPointTaskWorker) {
        Objects.requireNonNull(endPointTaskWorker);
        this.endPointTaskWorker = endPointTaskWorker;
    }

    @Override
    public ParameterAndValueAssigneeType[] getParameterTypeInfos() {
        return endPointTaskWorker.getParameterTypeInfos();
    }

    @Override
    public Optional<HttpTaskResult> execute(Object[] params) {
        Objects.requireNonNull(params);
        endPointTaskWorker.execute(params);
        return Optional.empty();
    }
}
