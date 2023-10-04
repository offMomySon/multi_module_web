package task;

import java.util.Objects;
import java.util.Optional;
import parameter.matcher.ParameterAndValueMatcherType;
import task.endpoint.EndPointTask;

public class HttpEmptyEndPointTask implements HttpEndPointTask{
    private final EndPointTask endPointTask;

    public HttpEmptyEndPointTask(EndPointTask endPointTask) {
        Objects.requireNonNull(endPointTask);
        this.endPointTask = endPointTask;
    }

    @Override
    public ParameterAndValueMatcherType[] getParameterTypeInfos() {
        return endPointTask.getParameterTypeInfos();
    }

    @Override
    public Optional<HttpTaskResult> execute(Object[] params) {
        Objects.requireNonNull(params);
        endPointTask.execute(params);
        return Optional.empty();
    }
}
