package method.support;

import annotation.RequestMapping;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import method.BaseHttpPathMatcher;
import method.PathUrlMatcher;
import method.segment.PathUrl;
import method.support.RequestMappingValueExtractor.RequestMappedMethod;
import util.AnnotationUtils;
import web.RequestMethod;

public class JavaMethodPathMatcherCreator {
    private final static Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    private final Class<?> clazz;
    private final RequestMappingValueExtractor valueExtractor;

    public JavaMethodPathMatcherCreator(@NonNull Class<?> clazz) {
        this.clazz = clazz;
        this.valueExtractor = new RequestMappingValueExtractor(clazz);
    }

    public List<BaseHttpPathMatcher> create() {
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
                PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(baseUrl);
                Method javaMethod = requestMappedMethod.getJavaMethod();

                return new BaseHttpPathMatcher(requestMethod, pathUrlMatcher, javaMethod);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
