package mapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

public class RequestMappingValueExtractor {
    private static final Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    public static List<RequestMappedMethod> extractRequestMappedMethods(Class<?> clazz, Method method) {
        if (Objects.isNull(clazz) || Objects.isNull(method)) {
            throw new RuntimeException("value is invalid.");
        }

        Optional<RequestMapping> clazzRequestMapping = AnnotationUtils.find(clazz, REQUEST_MAPPING_CLASS);
        RequestMapping methodRequestMapping = AnnotationUtils.find(method, REQUEST_MAPPING_CLASS)
            .orElseThrow(() -> new RuntimeException("method does not have RequestMapping."));

        List<HttpMethod> httpMethods = Arrays.stream(methodRequestMapping.method()).collect(Collectors.toUnmodifiableList());
        List<String> clazzUrls = clazzRequestMapping
            .map(c -> Arrays.asList(c.value()))
            .orElseGet(Collections::emptyList);
        List<String> methodUrls = Arrays.stream(methodRequestMapping.value())
            .collect(Collectors.toUnmodifiableList());

        List<String> fullMethodUrls = clazzUrls.stream()
            .flatMap(clazzUrl -> methodUrls.stream()
                .map(methodUrl -> clazzUrl + methodUrl))
            .collect(Collectors.toUnmodifiableList());

        return httpMethods.stream()
            .flatMap(httpMethod -> fullMethodUrls.stream().map(methodUrl -> new RequestMappedMethod(httpMethod, methodUrl, method)))
            .collect(Collectors.toUnmodifiableList());
    }

    @EqualsAndHashCode
    @Getter
    public static class RequestMappedMethod {
        private final HttpMethod httpMethod;
        private final String url;
        private final Method javaMethod;

        public RequestMappedMethod(HttpMethod httpMethod, String url, Method javaMethod) {
            if (Objects.isNull(httpMethod) || Objects.isNull(url) || url.isBlank() || url.isBlank()) {
                throw new RuntimeException("value is invalid.");
            }

            this.httpMethod = httpMethod;
            this.url = url;
            this.javaMethod = javaMethod;
        }
    }
}
