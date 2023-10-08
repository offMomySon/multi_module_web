package task.worker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import parameter.matcher.ParameterAndValueMatcherType;

@Slf4j
public class JavaMethodInvokeTaskWorker2 implements EndPointTaskWorker2 {
    private final WorkerResultType workerResultType;
    private final Object declaringClazzObject;
    private final Method javaMethod;
    private final ParameterAndValueMatcherType[] parameterAndValueMatcherTypes;

    public JavaMethodInvokeTaskWorker2(WorkerResultType workerResultType, Object declaringClazzObject, Method javaMethod, ParameterAndValueMatcherType[] _parameterAndValueMatcherTypes) {
        Objects.requireNonNull(workerResultType);
        Objects.requireNonNull(declaringClazzObject);
        Objects.requireNonNull(javaMethod);
        Objects.requireNonNull(_parameterAndValueMatcherTypes);

        Set<Parameter> methodParameters = Arrays.stream(javaMethod.getParameters()).collect(Collectors.toUnmodifiableSet());
        Set<Parameter> otherParameters = Arrays.stream(_parameterAndValueMatcherTypes).map(ParameterAndValueMatcherType::getParameter).collect(Collectors.toUnmodifiableSet());
        boolean doesNotMethodParameters = !methodParameters.containsAll(otherParameters) && methodParameters.size() == otherParameters.size();
        if (doesNotMethodParameters) {
            String errorMessage = MessageFormat.format("Does not method parameters. \nmethodParameters: `{}`\notherParameters: `{}`", methodParameters, otherParameters);
            throw new RuntimeException(errorMessage);
        }

        this.workerResultType = workerResultType;
        this.declaringClazzObject = declaringClazzObject;
        this.javaMethod = javaMethod;
        this.parameterAndValueMatcherTypes = _parameterAndValueMatcherTypes;
    }

    @Override
    public ParameterAndValueMatcherType[] getParameterTypeInfos() {
        return Arrays.copyOf(parameterAndValueMatcherTypes, parameterAndValueMatcherTypes.length);
    }

    @Override
    public WorkerResult execute(Object[] params) {
        Objects.requireNonNull(params);
        Object invoke = invokeMethod(this.declaringClazzObject, this.javaMethod, params);
        return new WorkerResult(workerResultType, invoke);
    }

    private static Object invokeMethod(Object declaringClazzObject, Method javaMethod, Object[] params){
        try {
            return javaMethod.invoke(declaringClazzObject, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
