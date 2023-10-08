package matcher;

import java.util.Objects;
import lombok.Getter;
import matcher.segment.PathVariableValue;
import task.worker.EndPointTaskWorker;
import task.worker.EndPointTaskWorker2;

@Getter
public class MatchedEndPointTaskWorker {
    private final EndPointTaskWorker endPointTaskWorker;
    private final PathVariableValue pathVariableValue;

    public MatchedEndPointTaskWorker(EndPointTaskWorker endPointTaskWorker, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(endPointTaskWorker);
        Objects.requireNonNull(pathVariableValue);
        this.endPointTaskWorker = endPointTaskWorker;
        this.pathVariableValue = pathVariableValue;
    }
}
