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

    public JavaMethodResolverCreator(@NonNull Class<?> clazz) {
        this.clazz = clazz;
    }

    public List<JavaMethodResolver> create() {
        // requestMapping 어노테이션이 존재하는 method 들을 수집한다.
        List<Method> peekMethods = AnnotationUtils.peekMethods(this.clazz, REQUEST_MAPPING_CLASS).stream()
            .collect(Collectors.toUnmodifiableList());

        RequestMappingValueExtractor requestMappingValueExtractor1 = new RequestMappingValueExtractor(this.clazz);

        List<RequestMappedMethod> requestMappedMethods = peekMethods.stream()
            // requestMappinghttpMethodUrlMethodCreator
            // 모든 문장을 표현한다.
            .map(requestMappingValueExtractor1::extractRequestMappedMethods)
            .flatMap(List::stream)
            .collect(Collectors.toUnmodifiableList());

        return requestMappedMethods.stream()
            .map(requestMappedMethod -> {
                HttpMethod httpMethod = requestMappedMethod.getHttpMethod();
                String url = requestMappedMethod.getUrl();
                Method javaMethod = requestMappedMethod.getJavaMethod();

                HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(httpMethod, url);

                return new JavaMethodResolver(javaMethod, httpMethodUrlMatcher);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
