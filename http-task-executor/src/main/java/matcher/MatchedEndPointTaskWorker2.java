package matcher;

import java.util.Objects;
import lombok.Getter;
import matcher.segment.PathVariableValue;
import task.worker.EndPointTaskWorker;
import task.worker.EndPointTaskWorker2;

@Getter
public class MatchedEndPointTaskWorker2 {
    private final EndPointTaskWorker2 endPointTaskWorker;
    private final PathVariableValue pathVariableValue;

    public MatchedEndPointTaskWorker2(EndPointTaskWorker2 endPointTaskWorker, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(endPointTaskWorker);
        Objects.requireNonNull(pathVariableValue);
        this.endPointTaskWorker = endPointTaskWorker;
        this.pathVariableValue = pathVariableValue;
    }
}
