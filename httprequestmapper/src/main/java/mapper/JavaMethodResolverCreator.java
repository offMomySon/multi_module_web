package mapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import mapper.RequestMappingHttpMethodUrlMethodCreator.HttpMethodUrlMethod;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

public class JavaMethodResolverCreator {
    private final static Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    private final Class<?> clazz;

    public JavaMethodResolverCreator(@NonNull Class<?> clazz) {
        this.clazz = clazz;
    }

    public List<JavaMethodResolver> create() {
        List<Method> methods = AnnotationUtils.peekMethods(this.clazz, REQUEST_MAPPING_CLASS).stream()
            .collect(Collectors.toUnmodifiableList());

        List<HttpMethodUrlMethod> httpMethodUrlMethods = methods.stream()
            .flatMap(method ->
                         RequestMappingHttpMethodUrlMethodCreator.create(this.clazz, method).stream())
            .collect(Collectors.toUnmodifiableList());

        return httpMethodUrlMethods.stream()
            .map(httpMethodUrlMethod -> {
                HttpMethod httpMethod = httpMethodUrlMethod.getHttpMethod();
                String url = httpMethodUrlMethod.getUrl();
                Method javaMethod = httpMethodUrlMethod.getJavaMethod();

                HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(httpMethod, url);

                return new JavaMethodResolver(javaMethod, httpMethodUrlMatcher);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
