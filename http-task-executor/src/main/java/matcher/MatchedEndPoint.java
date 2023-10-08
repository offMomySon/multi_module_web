package matcher;

import java.util.Objects;
import matcher.segment.PathVariableValue;
import task.HttpEndPointTask;

public class MatchedEndPoint {
    private final HttpEndPointTask httpEndPointTask;
    private final PathVariableValue pathVariableValue;

    public MatchedEndPoint(HttpEndPointTask httpEndPointTask, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(httpEndPointTask);
        Objects.requireNonNull(pathVariableValue);
        this.httpEndPointTask = httpEndPointTask;
        this.pathVariableValue = pathVariableValue;
    }

    public HttpEndPointTask getHttpEndPointTask() {
        return httpEndPointTask;
    }

    public PathVariableValue getPathVariableValue() {
        return pathVariableValue;
    }
}
