package matcher;

import java.lang.reflect.Method;
import java.util.Objects;
import matcher.segment.PathVariableValue;
import task.HttpTask;

public class MatchedHttpTask {
    private final HttpTask javaMethodTask;
    private final PathVariableValue pathVariableValue;

    public MatchedHttpTask(HttpTask javaMethodTask, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(javaMethodTask);
        Objects.requireNonNull(pathVariableValue);
        this.javaMethodTask = javaMethodTask;
        this.pathVariableValue = pathVariableValue;
    }

    public HttpTask getJavaMethodTask() {
        return javaMethodTask;
    }

    public PathVariableValue getPathVariableValue() {
        return pathVariableValue;
    }
}
