package method.support;

import annotation.RequestMapping;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import util.AnnotationUtils;
import web.RequestMethod;

public class RequestMappingValueExtractor {
    private static final Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    private final Class<?> clazz;
    private final Set<Method> methods;

    public RequestMappingValueExtractor(@NonNull Class<?> clazz) {
        this.clazz = clazz;
        this.methods = AnnotationUtils.peekMethods(this.clazz, REQUEST_MAPPING_CLASS).stream()
            .collect(Collectors.toUnmodifiableSet());
    }

    public List<RequestMappedMethod> extractRequestMappedMethods(Method javaMethod) {
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
            .map(c -> Arrays.asList(c.value()))
            .orElseGet(Collections::emptyList);
        List<String> methodUrls = Arrays.stream(methodRequestMapping.value())
            .collect(Collectors.toUnmodifiableList());

        List<String> fullMethodUrls = clazzUrls.stream()
            .flatMap(clazzUrl -> methodUrls.stream()
                .map(methodUrl -> clazzUrl + methodUrl))
            .collect(Collectors.toUnmodifiableList());

        return requestMethods.stream()
            .flatMap(httpMethod -> fullMethodUrls.stream()
                .map(methodUrl -> new RequestMappedMethod(httpMethod, methodUrl, javaMethod)))
            .collect(Collectors.toUnmodifiableList());
    }

    @EqualsAndHashCode
    @Getter
    public static class RequestMappedMethod {
        private final RequestMethod requestMethod;
        private final String url;
        private final Method javaMethod;

        public RequestMappedMethod(RequestMethod requestMethod, String url, Method javaMethod) {
            if (Objects.isNull(requestMethod) || Objects.isNull(javaMethod) ||
                Objects.isNull(url) || url.isBlank() || url.isBlank()) {
                throw new RuntimeException("value is invalid.");
            }

            this.requestMethod = requestMethod;
            this.url = url;
            this.javaMethod = javaMethod;
        }
    }
}
