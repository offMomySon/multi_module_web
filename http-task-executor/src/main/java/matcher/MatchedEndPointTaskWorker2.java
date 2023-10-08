package matcher;

import java.util.Objects;
import lombok.Getter;
import matcher.segment.PathVariableValue;
import task.worker.EndPointTaskWorker;

@Getter
public class MatchedEndPointTaskWorker2 {
    private final EndPointTaskWorker endPointTaskWorker;
    private final PathVariableValue pathVariableValue;

    public MatchedEndPointTaskWorker2(EndPointTaskWorker endPointTaskWorker, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(endPointTaskWorker);
        Objects.requireNonNull(pathVariableValue);
        this.endPointTaskWorker = endPointTaskWorker;
        this.pathVariableValue = pathVariableValue;
    }
}
