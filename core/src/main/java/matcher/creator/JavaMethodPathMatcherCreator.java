package matcher.creator;

import com.main.util.AnnotationUtils;
import instance.ReadOnlyObjectRepository;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import matcher.JavaMethodEndpointTaskMatcher;
import matcher.PathUrlMatcher;
import matcher.RequestMethod;
import annotation.RequestMapping;
import matcher.creator.RequestMappingValueExtractor.RequestMappedMethod;
import matcher.segment.PathUrl;
import matcher.segment.creator.SegmentChunkFactory;

public class JavaMethodPathMatcherCreator {
    private final static Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    private final Class<?> clazz;
    private final RequestMappingValueExtractor valueExtractor;
    private final ReadOnlyObjectRepository objectRepository;

    public JavaMethodPathMatcherCreator(@NonNull Class<?> clazz, ReadOnlyObjectRepository objectRepository) {
        this.clazz = clazz;
        this.valueExtractor = new RequestMappingValueExtractor(clazz);
        this.objectRepository = objectRepository;
    }

    public List<JavaMethodEndpointTaskMatcher> create() {
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
                SegmentChunkFactory segmentChunkFactory = new SegmentChunkFactory(baseUrl);
                PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(segmentChunkFactory);

                Method javaMethod = requestMappedMethod.getJavaMethod();
                Class<?> declaringClass = javaMethod.getDeclaringClass();
                Object declaringInstance = objectRepository.get(declaringClass);

                return new JavaMethodEndpointTaskMatcher(requestMethod, pathUrlMatcher, declaringInstance, javaMethod);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
