package mapper;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import mapper.RequestMappingHttpMethodUrlMethodCreator.HttpMethodUrlMethod;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

public class RequestMappingHttpMethodUrlMethodResolverExtractor {
    private final static Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    public static List<JavaMethodResolver> extract(Class<?> clazz) {
        if(Objects.isNull(clazz)){
            return Collections.emptyList();
        }

        List<Method> methods = AnnotationUtils.peekMethods(clazz, REQUEST_MAPPING_CLASS).stream()
            .collect(Collectors.toUnmodifiableList());

        List<HttpMethodUrlMethod> httpMethodUrlMethods = methods.stream()
            .flatMap(method ->
                         RequestMappingHttpMethodUrlMethodCreator.create(clazz, method).stream())
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
