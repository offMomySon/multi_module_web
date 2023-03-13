package mapper;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import mapper.RequestMappingValueExtractor.RequestMappedMethod;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

public class RequestMappingHttpMethodUrlMethodResolverExtractor {
    private final static Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

//    public static List<HttpMethodUrlMethodResolver> extract(Class<?> clazz) {
//        if(Objects.isNull(clazz)){
//            return Collections.emptyList();
//        }
//
//        List<Method> methods = AnnotationUtils.peekMethods(clazz, REQUEST_MAPPING_CLASS).stream()
//            .collect(Collectors.toUnmodifiableList());
//
//        List<RequestMappedMethod> requestMappedMethods = methods.stream()
//            .flatMap(method ->
//                         RequestMappingValueExtractor.extractRequestMappedMethods(clazz, method).stream())
//            .collect(Collectors.toUnmodifiableList());
//
//        return requestMappedMethods.stream()
//            .map(requestMappedMethod -> {
//                HttpMethod httpMethod = requestMappedMethod.getHttpMethod();
//                String url = requestMappedMethod.getUrl();
//                Method method = requestMappedMethod.getJavaMethod();
//
//                HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(httpMethod, url);
//
//                return new HttpMethodUrlMethodResolver(method, httpMethodUrlMatcher);
//            })
//            .collect(Collectors.toUnmodifiableList());
//    }


    public static List<HttpMethodUrlMethodResolver> extract2(Class<?> clazz) {
        if(Objects.isNull(clazz)){
            return Collections.emptyList();
        }

        List<Method> methods = AnnotationUtils.peekMethods(clazz, REQUEST_MAPPING_CLASS).stream()
            .collect(Collectors.toUnmodifiableList());

        RequestMappingValueExtractor2 requestMappingValueExtractor2 = RequestMappingValueExtractor2.from(clazz);

        List<RequestMappingValueExtractor2.RequestMappedMethod> requestMappedMethods = methods.stream()
            .map(requestMappingValueExtractor2::extractRequestMappedMethods)
            .flatMap(List::stream)
            .collect(Collectors.toUnmodifiableList());

        return requestMappedMethods.stream()
            .map(requestMappedMethod -> {
                HttpMethod httpMethod = requestMappedMethod.getHttpMethod();
                String url = requestMappedMethod.getUrl();
                Method method = requestMappedMethod.getJavaMethod();

                HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(httpMethod, url);

                return new HttpMethodUrlMethodResolver(method, httpMethodUrlMatcher);
            })
            .collect(Collectors.toUnmodifiableList());
    }

    public static List<HttpMethodUrlMethodResolver> extract3(Class<?> clazz) {
        if(Objects.isNull(clazz)){
            return Collections.emptyList();
        }

        RequestMappingValueExtractor3 requestMappingValueExtractor3 = RequestMappingValueExtractor3.from(clazz);

        List<RequestMappingValueExtractor3.RequestMappedMethod> requestMappedMethods = requestMappingValueExtractor3.extractRequestMappedMethods();

        return requestMappedMethods.stream()
            .map(requestMappedMethod -> {
                HttpMethod httpMethod = requestMappedMethod.getHttpMethod();
                String url = requestMappedMethod.getUrl();
                Method method = requestMappedMethod.getJavaMethod();

                HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(httpMethod, url);

                return new HttpMethodUrlMethodResolver(method, httpMethodUrlMatcher);
            })
            .collect(Collectors.toUnmodifiableList());
    }


}
