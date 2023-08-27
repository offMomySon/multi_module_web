package matcher;

import java.lang.reflect.Method;
import java.util.Objects;
import matcher.segment.PathVariableValue;

public class MatchedMethod {
    private final Method javaMethod;
    private final PathVariableValue pathVariableValue;

    public MatchedMethod(Method javaMethod, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(javaMethod);
        Objects.requireNonNull(pathVariableValue);
        this.javaMethod = javaMethod;
        this.pathVariableValue = pathVariableValue;
    }

    public Method getJavaMethod() {
        return javaMethod;
    }

    public PathVariableValue getPathVariableValue() {
        return pathVariableValue;
    }
}