package matcher;

import java.util.Objects;
import lombok.Getter;
import matcher.segment.PathVariableValue;
import task.HttpEndPointTask;
import task.worker.EndPointTaskWorker;

@Getter
public class MatchedEndPoint2 {
    private final EndPointTaskWorker endPointTaskWorker;
    private final PathVariableValue pathVariableValue;

    public MatchedEndPoint2(EndPointTaskWorker endPointTaskWorker, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(endPointTaskWorker);
        Objects.requireNonNull(pathVariableValue);
        this.endPointTaskWorker = endPointTaskWorker;
        this.pathVariableValue = pathVariableValue;
    }
}
