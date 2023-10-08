package matcher.creator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import parameter.matcher.ParameterAndValueAssigneeType;
import task.worker.JavaMethodInvokeTaskWorker2;
import task.worker.WorkerResultType;
import task.worker.EndPointTaskWorker;
import task.worker.JavaMethodInvokeTaskWorker;

public class JavaMethodInvokeTaskWorkerCreator2 {
    private final Function<Parameter, ParameterAndValueAssigneeType> parameterParameterAndValueAssigneeTypeFunction;

    public JavaMethodInvokeTaskWorkerCreator2(Function<Parameter, ParameterAndValueAssigneeType> parameterParameterAndValueAssigneeTypeFunction) {
        Objects.requireNonNull(parameterParameterAndValueAssigneeTypeFunction);
        this.parameterParameterAndValueAssigneeTypeFunction = parameterParameterAndValueAssigneeTypeFunction;
    }

    public JavaMethodInvokeTaskWorker2 create(Object object, Method javaMethod) {
        if (Objects.isNull(object) || Objects.isNull(javaMethod)) {
            throw new RuntimeException("Invalid parameter. parameter is empty.");
        }

        ParameterAndValueAssigneeType[] parameterAndValueAssigneeTypes = Arrays.stream(javaMethod.getParameters())
            .map(parameterParameterAndValueAssigneeTypeFunction)
            .toArray(ParameterAndValueAssigneeType[]::new);

        Class<?> returnType = javaMethod.getReturnType();
        WorkerResultType workerResultType = WorkerResultType.findByClazz(returnType);
        return new JavaMethodInvokeTaskWorker2(workerResultType, object, javaMethod, parameterAndValueAssigneeTypes);
    }
}
