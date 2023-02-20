package mapper;

import java.lang.reflect.Method;
import mapper.marker.RequestMapping;

public class RequestMappingMethodDto {
    private final RequestMapping clazzRequestMapping;
    private final RequestMapping methodRequestMapping;
    private final Method method;

    public RequestMappingMethodDto(RequestMapping clazzRequestMapping, RequestMapping methodRequestMapping, Method method) {
        this.clazzRequestMapping = clazzRequestMapping;
        this.methodRequestMapping = methodRequestMapping;
        this.method = method;
    }

    public RequestMapping getClazzRequestMapping() {
        return clazzRequestMapping;
    }

    public RequestMapping getMethodRequestMapping() {
        return methodRequestMapping;
    }

    public Method getMethod() {
        return method;
    }
}
