package matcher;

import java.util.Objects;
import matcher.segment.PathVariableValue;
import task.Task;

public class MatchedEndPoint {
    private final Task task;
    private final PathVariableValue pathVariableValue;

    public MatchedEndPoint(Task task, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(pathVariableValue);
        this.task = task;
        this.pathVariableValue = pathVariableValue;
    }

    public Task getTask() {
        return task;
    }

    public PathVariableValue getPathVariableValue() {
        return pathVariableValue;
    }
}
