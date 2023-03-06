package mapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

public class RequestMappingHttpMethodUrlMethodCreator {
    private static final Class<RequestMapping> REQUEST_MAPPING_CLASS = RequestMapping.class;

    public static List<HttpMethodUrlMethod> create(Class<?> clazz, Method method) {
        if (Objects.isNull(clazz) || Objects.isNull(method)) {
            throw new RuntimeException("value is invalid.");
        }

        RequestMapping clazzRequestMapping = AnnotationUtils.find(clazz, REQUEST_MAPPING_CLASS)
            .orElseThrow(() -> new RuntimeException("clazz does not have RequestMapping."));
        RequestMapping methodRequestMapping = AnnotationUtils.find(method, REQUEST_MAPPING_CLASS)
            .orElseThrow(() -> new RuntimeException("method does not have RequestMapping."));

        List<HttpMethod> httpMethods = Arrays.stream(methodRequestMapping.method()).collect(Collectors.toUnmodifiableList());
        List<String> methodUrls = Arrays.stream(clazzRequestMapping.value())
            .flatMap(clazzUrl -> Arrays.stream(methodRequestMapping.value())
                .map(methodUrl -> clazzUrl + methodUrl))
            .collect(Collectors.toUnmodifiableList());

        return httpMethods.stream()
            .flatMap(httpMethod -> methodUrls.stream().map(methodUrl -> new HttpMethodUrlMethod(httpMethod, methodUrl, method)))
            .collect(Collectors.toUnmodifiableList());
    }

    @EqualsAndHashCode
    @Getter
    public static class HttpMethodUrlMethod {
        private final HttpMethod httpMethod;
        private final String url;
        private final Method method;

        public HttpMethodUrlMethod(HttpMethod httpMethod, String url, Method method) {
            if (Objects.isNull(httpMethod) || Objects.isNull(url) || url.isBlank() || url.isBlank()) {
                throw new RuntimeException("value is invalid.");
            }

            this.httpMethod = httpMethod;
            this.url = url;
            this.method = method;
        }
    }
}
