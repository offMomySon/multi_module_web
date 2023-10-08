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
import matcher.segment.PathUrl;
import matcher.segment.creator.SegmentChunkFactory;
import parameter.matcher.ParameterAndValueMatcherType;
import task.HttpConvertEndPointTask;
import task.HttpEmptyEndPointTask;
import task.HttpEndPointTask;
import task.HttpTextEndPointTask;
import task.worker.EndPointTaskWorker;
import task.worker.JavaMethodInvokeTaskWorker;
import task.worker.WorkerContentType;
import vo.ContentType;

public class JavaMethodPathMatcherCreator {
    private final Function<Parameter, ParameterAndValueMatcherType> parameterParameterTypeInfoFunction;

    public JavaMethodPathMatcherCreator(Function<Parameter, ParameterAndValueMatcherType> parameterParameterTypeInfoFunction) {
        Objects.requireNonNull(parameterParameterTypeInfoFunction);
        this.parameterParameterTypeInfoFunction = parameterParameterTypeInfoFunction;
    }

    public JavaMethodEndpointTaskMatcher create(RequestMappedMethod requestMappedMethod) {
        if (Objects.isNull(requestMappedMethod)) {
            throw new RuntimeException("requestMappedMethod is empty.");
        }

        RequestMethod requestMethod = requestMappedMethod.getRequestMethod();

        PathUrl baseUrl = PathUrl.from(requestMappedMethod.getUrl());
        SegmentChunkFactory segmentChunkFactory = new SegmentChunkFactory(baseUrl);
        PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(segmentChunkFactory);

        Object object = requestMappedMethod.getObject();
        Method javaMethod = requestMappedMethod.getJavaMethod();
        ParameterAndValueMatcherType[] parameterAndValueMatcherTypes = Arrays.stream(javaMethod.getParameters())
            .map(parameterParameterTypeInfoFunction)
            .toArray(ParameterAndValueMatcherType[]::new);

        Class<?> returnType = javaMethod.getReturnType();
        WorkerContentType workerContentType = WorkerContentType.findByClazz(returnType);
        EndPointTaskWorker endPointTaskWorker = new JavaMethodInvokeTaskWorker(workerContentType, object, javaMethod, parameterAndValueMatcherTypes);

        HttpEndPointTask httpEndPointTask;
        if (returnType == Void.TYPE) {
            httpEndPointTask = new HttpEmptyEndPointTask(endPointTaskWorker);
        } else if (returnType == String.class) {
            httpEndPointTask = new HttpTextEndPointTask(endPointTaskWorker);
        } else {
            ValueConverter valueConverter = new ObjectValueConverter(returnType);
            httpEndPointTask = new HttpConvertEndPointTask(ContentType.APPLICATION_JSON, valueConverter, endPointTaskWorker);
        }

        return new JavaMethodEndpointTaskMatcher(requestMethod, pathUrlMatcher, httpEndPointTask);
    }
}
