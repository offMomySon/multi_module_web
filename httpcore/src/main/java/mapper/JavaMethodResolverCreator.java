package mapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import mapper.RequestMappingValueExtractor.RequestMappedMethod;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

public class JavaMethodResolverCreator {
    private final static Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    private final Class<?> clazz;
    private final RequestMappingValueExtractor valueExtractor;

    public JavaMethodResolverCreator(@NonNull Class<?> clazz) {
        this.clazz = clazz;
        this.valueExtractor = new RequestMappingValueExtractor(clazz);
    }

    public List<HttpPathMatcher> create() {
        List<Method> peekMethods = AnnotationUtils.peekMethods(this.clazz, REQUEST_MAPPING_CLASS).stream()
            .collect(Collectors.toUnmodifiableList());

        List<RequestMappedMethod> requestMappedMethods = peekMethods.stream()
            .map(valueExtractor::extractRequestMappedMethods)
            .flatMap(List::stream)
            .collect(Collectors.toUnmodifiableList());

        return requestMappedMethods.stream()
            .map(requestMappedMethod -> {
                HttpMethod httpMethod = requestMappedMethod.getHttpMethod();
                String url = requestMappedMethod.getUrl();
                Method javaMethod = requestMappedMethod.getJavaMethod();


                return new HttpPathMatcher(httpMethod, url, javaMethod);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
