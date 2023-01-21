package mapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.ToString;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

@ToString
public class MethodHandler {
    private final List<MethodIndicator> methodIndicators;
    private final Method method;


    public MethodHandler(List<MethodIndicator> methodIndicators, Method method) {
        if (Objects.isNull(methodIndicators)) {
            throw new RuntimeException("methodIndicator is null.");
        }

        this.methodIndicators = methodIndicators.stream().filter(methodIndicator -> !Objects.isNull(methodIndicator)).collect(Collectors.toUnmodifiableList());
        this.method = method;
    }

    public static MethodHandler from(Set<String> prefixUrls, Method method) {
        RequestMapping requestMapping = AnnotationUtils.find(method, RequestMapping.class)
            .orElseThrow(() -> new RuntimeException("requestMapping 이 존재하지 않습니다."));

        Set<HttpMethod> httpMethods = Arrays.stream(requestMapping.method()).collect(Collectors.toUnmodifiableSet());
        Set<String> methodUris = Arrays.stream(requestMapping.value())
            .flatMap(methodUrl -> prefixUrls.stream().map(prefixUrl -> prefixUrl + methodUrl))
            .collect(Collectors.toUnmodifiableSet());

        List<MethodIndicator> methodIndicators = new ArrayList<>();
        for (HttpMethod httpMethod : httpMethods) {
            for (String methodUri : methodUris) {
                MethodIndicator methodIndicator = new MethodIndicator(httpMethod, methodUri);
                methodIndicators.add(methodIndicator);
            }
        }

        return new MethodHandler(methodIndicators, method);
    }

    public boolean isIndicated(MethodIndicator otherMethodIndicator) {
        return methodIndicators.stream()
            .anyMatch(methodIndicator -> methodIndicator.equals(otherMethodIndicator));
    }

}
