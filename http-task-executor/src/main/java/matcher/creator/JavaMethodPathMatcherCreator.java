package matcher.creator;

import converter.ValueConverter;
import converter.ObjectValueConverter;
import java.lang.reflect.Method;
import java.util.Objects;
import matcher.JavaMethodEndpointTaskMatcher;
import matcher.PathUrlMatcher;
import matcher.RequestMethod;
import matcher.segment.PathUrl;
import matcher.segment.creator.SegmentChunkFactory;
import task.HttpConvertEndPointTask;
import task.HttpEmptyEndPointTask;
import task.HttpEndPointTask;
import task.HttpTextEndPointTask;
import task.endpoint.EndPointTask;
import task.endpoint.JavaMethodInvokeTask;
import vo.ContentType;

public class JavaMethodPathMatcherCreator {
    public static JavaMethodEndpointTaskMatcher create(RequestMappedMethod requestMappedMethod){
        if(Objects.isNull(requestMappedMethod)){
            throw new RuntimeException("requestMappedMethod is empty.");
        }

        RequestMethod requestMethod = requestMappedMethod.getRequestMethod();

        PathUrl baseUrl = PathUrl.from(requestMappedMethod.getUrl());
        SegmentChunkFactory segmentChunkFactory = new SegmentChunkFactory(baseUrl);
        PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(segmentChunkFactory);

        Object object = requestMappedMethod.getObject();
        Method javaMethod = requestMappedMethod.getJavaMethod();
        EndPointTask endPointTask = new JavaMethodInvokeTask(object, javaMethod);

        Class<?> returnType = javaMethod.getReturnType();
        HttpEndPointTask httpEndPointTask;
        if(returnType == Void.TYPE){
            httpEndPointTask = new HttpEmptyEndPointTask(endPointTask);
        } else if(returnType == String.class) {
            httpEndPointTask = new HttpTextEndPointTask(endPointTask);
        } else {
            ValueConverter valueConverter = new ObjectValueConverter(returnType);
            httpEndPointTask = new HttpConvertEndPointTask(ContentType.APPLICATION_JSON, valueConverter, endPointTask);
        }

        return new JavaMethodEndpointTaskMatcher(requestMethod, pathUrlMatcher, httpEndPointTask);
    }
}
