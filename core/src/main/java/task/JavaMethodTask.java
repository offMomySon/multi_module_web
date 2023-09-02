package task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaMethodTask implements HttpTask {
    private final Object declaringClazzObject;
    private final Method javaMethod;
    private final Parameter[] parameters;

    private JavaMethodTask(Object declaringClazzObject, Method javaMethod) {
        Objects.requireNonNull(declaringClazzObject);
        Objects.requireNonNull(javaMethod);
        this.declaringClazzObject = declaringClazzObject;
        this.javaMethod = javaMethod;
        this.parameters = javaMethod.getParameters();
    }

    @Override
    public Parameter[] getExecuteParameters() {
        return Arrays.copyOf(parameters, parameters.length);
    }

    @Override
    public Optional<Object> execute(Object[] params) {
        try {
            Object invoke = javaMethod.invoke(declaringClazzObject, params);
            return Optional.ofNullable(invoke);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
