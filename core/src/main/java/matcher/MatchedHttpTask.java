package matcher;

import java.util.Objects;
import matcher.segment.PathVariableValue;
import task.HttpTask;

public class MatchedHttpTask {
    private final HttpTask httpTask;
    private final PathVariableValue pathVariableValue;

    public MatchedHttpTask(HttpTask httpTask, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(httpTask);
        Objects.requireNonNull(pathVariableValue);
        this.httpTask = httpTask;
        this.pathVariableValue = pathVariableValue;
    }

    public HttpTask getHttpTask() {
        return httpTask;
    }

    public PathVariableValue getPathVariableValue() {
        return pathVariableValue;
    }
}
