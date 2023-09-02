package matcher.creator;

import container.ObjectRepository;
import matcher.JavaMethodEndpointMatcher;
import matcher.PathUrlMatcher;
import matcher.RequestMethod;
import matcher.annotation.RequestMapping;
import matcher.segment.PathUrl;
import matcher.segment.SegmentChunkFactory;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import util.AnnotationUtils;

public class JavaMethodPathMatcherCreator {
    private final static Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    private final Class<?> clazz;
    private final RequestMappingValueExtractor valueExtractor;
    private final ObjectRepository objectRepository;

    public JavaMethodPathMatcherCreator(@NonNull Class<?> clazz, @NonNull ObjectRepository objectRepository) {
        this.clazz = clazz;
        this.valueExtractor = new RequestMappingValueExtractor(clazz);
        this.objectRepository = objectRepository;
    }

    public List<JavaMethodEndpointMatcher> create() {
        List<Method> peekMethods = AnnotationUtils.peekMethods(this.clazz, REQUEST_MAPPING_CLASS).stream()
            .collect(Collectors.toUnmodifiableList());

        List<RequestMappingValueExtractor.RequestMappedMethod> requestMappedMethods = peekMethods.stream()
            .map(valueExtractor::extractRequestMappedMethods)
            .flatMap(List::stream)
            .collect(Collectors.toUnmodifiableList());

        return requestMappedMethods.stream()
            .map(requestMappedMethod -> {
                RequestMethod requestMethod = requestMappedMethod.getRequestMethod();
                PathUrl baseUrl = PathUrl.from(requestMappedMethod.getUrl());
                Method javaMethod = requestMappedMethod.getJavaMethod();

                SegmentChunkFactory segmentChunkFactory = new SegmentChunkFactory(baseUrl);
                PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(segmentChunkFactory);

                Class<?> declaringClass = javaMethod.getDeclaringClass();
                Object declaringInstance = objectRepository.get(declaringClass);

                return new JavaMethodEndpointMatcher(requestMethod, pathUrlMatcher, declaringInstance, javaMethod);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
