package mapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

public class MethodMatcherCreator {
    private final static Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    private final Class<?> clazz;
    private final RequestMappingValueExtractor2 requestMappingValueExtractor2;

    public MethodMatcherCreator(Class<?> clazz) {
        this.clazz = clazz;
        this.requestMappingValueExtractor2 = new RequestMappingValueExtractor2(clazz);
    }

    public List<MethodMatcher2> craete() {
        List<Method> methods = AnnotationUtils.peekMethods(clazz, REQUEST_MAPPING_CLASS).stream()
            .collect(Collectors.toUnmodifiableList());

        List<RequestMappingValueExtractor2.RequestMappedMethod> requestMappedMethods = methods.stream()
            .map(requestMappingValueExtractor2::extractRequestMappedMethods)
            .flatMap(List::stream)
            .collect(Collectors.toUnmodifiableList());

        return requestMappedMethods.stream()
            .map(requestMappedMethod -> {
                HttpMethod httpMethod = requestMappedMethod.getHttpMethod();
                String url = requestMappedMethod.getUrl();
                Method method = requestMappedMethod.getJavaMethod();

                return new MethodMatcher2(httpMethod, url, method);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
