package matcher.creator;

import com.main.util.AnnotationUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import matcher.RequestMethod;
import annotation.RequestMapping;

public class RequestMappingValueExtractor {
    private static final Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    private final Class<?> clazz;
    private final Set<Method> methods;

    public RequestMappingValueExtractor(@NonNull Class<?> clazz) {
        this.clazz = clazz;
        this.methods = AnnotationUtils.peekAllAnnotatedMethods(this.clazz, REQUEST_MAPPING_CLASS).stream()
            .collect(Collectors.toUnmodifiableSet());
    }

    public List<EndPointMethodInfo> extractRequestMappedMethods(Method javaMethod) {
        if (Objects.isNull(javaMethod)) {
            return Collections.emptyList();
        }
        if (!methods.contains(javaMethod)) {
            return Collections.emptyList();
        }

        Optional<RequestMapping> clazzRequestMapping = AnnotationUtils.find(clazz, REQUEST_MAPPING_CLASS);
        RequestMapping methodRequestMapping = AnnotationUtils.find(javaMethod, REQUEST_MAPPING_CLASS)
            .orElseThrow(() -> new RuntimeException("method does not have RequestMapping."));

        List<RequestMethod> requestMethods = Arrays.stream(methodRequestMapping.method()).collect(Collectors.toUnmodifiableList());
        List<String> clazzUrls = clazzRequestMapping
            .map(c -> Arrays.asList(c.url()))
            .orElseGet(Collections::emptyList);
        List<String> methodUrls = Arrays.stream(methodRequestMapping.url())
            .collect(Collectors.toUnmodifiableList());

        List<String> fullMethodUrls = clazzUrls.stream()
            .flatMap(clazzUrl -> methodUrls.stream()
                .map(methodUrl -> clazzUrl + methodUrl))
            .collect(Collectors.toUnmodifiableList());

        return requestMethods.stream()
            .flatMap(httpMethod -> fullMethodUrls.stream()
                .map(methodUrl -> new EndPointMethodInfo(httpMethod, methodUrl, null, javaMethod)))
            .collect(Collectors.toUnmodifiableList());
    }
}
