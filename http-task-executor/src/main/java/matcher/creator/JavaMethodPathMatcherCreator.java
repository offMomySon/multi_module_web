package matcher.creator;

import annotation.RequestMapping;
import com.main.util.AnnotationUtils;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;
import matcher.JavaMethodEndpointTaskMatcher;
import matcher.PathUrlMatcher;
import matcher.RequestMethod;
import matcher.creator.RequestMappingValueExtractor.RequestMappedMethod;
import matcher.segment.PathUrl;
import matcher.segment.creator.SegmentChunkFactory;

public class JavaMethodPathMatcherCreator {
    private final static Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    private final Class<?> clazz;
    private final Object clazzObject;
    private final RequestMappingValueExtractor valueExtractor;

    public JavaMethodPathMatcherCreator(@NonNull Class<?> clazz, Object clazzObject) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(clazzObject);

        boolean doesNotClazzInstance = clazz != clazzObject.getClass();
        if (doesNotClazzInstance) {
            throw new RuntimeException("object must be clazz instance.");
        }

        this.clazz = clazz;
        this.clazzObject = clazzObject;
        this.valueExtractor = new RequestMappingValueExtractor(clazz);
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

                return new JavaMethodEndpointTaskMatcher(requestMethod, pathUrlMatcher, clazzObject, javaMethod);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
