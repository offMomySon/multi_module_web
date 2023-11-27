package matcher.creator;

import converter.ObjectValueConverter;
import converter.ValueConverter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import matcher.JavaMethodEndpointTaskMatcher;
import matcher.PathUrlMatcher;
import matcher.RequestMethod;
import matcher.segment.PathUrl2;
import matcher.segment.factory.SegmentChunkFactory;
import parameter.matcher.ParameterAndValueAssigneeType;
import task.HttpConvertEndPointTask;
import task.HttpEmptyEndPointTask;
import task.HttpEndPointTask;
import task.HttpTextEndPointTask;
import task.worker.EndPointTaskWorker;
import task.worker.JavaMethodInvokeTaskWorker;
import task.worker.WorkerResultType;

public class JavaMethodPathMatcherCreator {
    private final Function<Parameter, ParameterAndValueAssigneeType> parameterParameterTypeInfoFunction;

    public JavaMethodPathMatcherCreator(Function<Parameter, ParameterAndValueAssigneeType> parameterParameterTypeInfoFunction) {
        Objects.requireNonNull(parameterParameterTypeInfoFunction);
        this.parameterParameterTypeInfoFunction = parameterParameterTypeInfoFunction;
    }

    public JavaMethodEndpointTaskMatcher create(EndPointMethodInfo endPointMethodInfo) {
        if (Objects.isNull(endPointMethodInfo)) {
            throw new RuntimeException("requestMappedMethod is empty.");
        }

        RequestMethod requestMethod = endPointMethodInfo.getRequestMethod();

        PathUrl2 baseUrl = PathUrl2.from(endPointMethodInfo.getUrl());
        SegmentChunkFactory segmentChunkFactory = new SegmentChunkFactory(baseUrl);
        PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(segmentChunkFactory);

        Object object = endPointMethodInfo.getObject();
        Method javaMethod = endPointMethodInfo.getJavaMethod();
        ParameterAndValueAssigneeType[] parameterAndValueAssigneeTypes = Arrays.stream(javaMethod.getParameters())
            .map(parameterParameterTypeInfoFunction)
            .toArray(ParameterAndValueAssigneeType[]::new);

        Class<?> returnType = javaMethod.getReturnType();
        WorkerResultType workerResultType = WorkerResultType.findByClazz(returnType);
        EndPointTaskWorker endPointTaskWorker = new JavaMethodInvokeTaskWorker(workerResultType, object, javaMethod, parameterAndValueAssigneeTypes);

        HttpEndPointTask httpEndPointTask;
        if (returnType == Void.TYPE) {
            httpEndPointTask = new HttpEmptyEndPointTask(endPointTaskWorker);
        } else if (returnType == String.class) {
            httpEndPointTask = new HttpTextEndPointTask(endPointTaskWorker);
        } else {
            ValueConverter valueConverter = new ObjectValueConverter(returnType);
            httpEndPointTask = new HttpConvertEndPointTask(vo.ContentType.APPLICATION_JSON, valueConverter, endPointTaskWorker);
        }

        return new JavaMethodEndpointTaskMatcher(requestMethod, pathUrlMatcher, httpEndPointTask);
    }
}
