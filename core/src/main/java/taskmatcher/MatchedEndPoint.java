package taskmatcher;

import java.util.Objects;
import taskmatcher.segment.PathVariableValue;
import task.EndPointTask;

public class MatchedEndPoint {
    private final EndPointTask endPointTask;
    private final PathVariableValue pathVariableValue;

    public MatchedEndPoint(EndPointTask endPointTask, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(endPointTask);
        Objects.requireNonNull(pathVariableValue);
        this.endPointTask = endPointTask;
        this.pathVariableValue = pathVariableValue;
    }

    public EndPointTask getTask() {
        return endPointTask;
    }

    public PathVariableValue getPathVariableValue() {
        return pathVariableValue;
    }
}
