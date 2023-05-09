package mapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import mapper.RequestMappingValueExtractor.RequestMappedMethod;
import mapper.segmentv3.PathUrl;
import marker.RequestMapping;
import marker.RequestMethod;
import util.AnnotationUtils;

public class JavaMethodPathMatcherCreator {
    private final static Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    private final Class<?> clazz;
    private final RequestMappingValueExtractor valueExtractor;

    public JavaMethodPathMatcherCreator(@NonNull Class<?> clazz) {
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
                RequestMethod requestMethod = requestMappedMethod.getRequestMethod();
                PathUrl baseUrl = PathUrl.from(requestMappedMethod.getUrl());
                Method javaMethod = requestMappedMethod.getJavaMethod();

                return HttpPathMatcher.from(requestMethod, baseUrl, javaMethod);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
