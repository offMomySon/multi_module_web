package mapper;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.ToString;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

/**
 * 역할.
 * method 를 가져오는 역할.
 *
 * handle - 다루다.
 * -> method 를 가지고 연관된 행위들을 다룬다.
 */
@ToString
public class MethodResolver {
    private final List<MethodIndicator> methodIndicators;
    private final Class clazz;
    private final Method method;

    public MethodResolver(List<MethodIndicator> methodIndicators, Class clazz, Method method) {
        if (Objects.isNull(methodIndicators)) {
            throw new RuntimeException("methodIndicator is null.");
        }

        this.methodIndicators = methodIndicators.stream()
            .filter(methodIndicator -> !Objects.isNull(methodIndicator))
            .collect(Collectors.toUnmodifiableList());
        this.method = method;
        this.clazz = clazz;
    }

    public static MethodResolver from(Class clazz, Method method) {
        validateEmtpy(clazz);
        validateEmtpy(method);
        AnnotationUtils.find(clazz, Controller.class).orElseThrow(() -> new RuntimeException("controller annotation does not exist."));

        RequestMapping controllerRequestMapping = AnnotationUtils.find(clazz, RequestMapping.class).orElseThrow(() -> new RuntimeException("requestMapping annotation does not exist."));
        RequestMapping methodRequestMapping = AnnotationUtils.find(method, RequestMapping.class).orElseThrow(() -> new RuntimeException("requestMapping annotation does not exist."));

        Set<HttpMethod> methodHttpMethods = Arrays.stream(methodRequestMapping.method())
            .collect(Collectors.toUnmodifiableSet());
        Set<String> controllerUrls = Arrays.stream(controllerRequestMapping.value()).collect(Collectors.toUnmodifiableSet());
        Set<String> methodUrls = Arrays.stream(methodRequestMapping.value()).collect(Collectors.toUnmodifiableSet());

        List<MethodIndicator> methodIndicators = new ArrayList<>();
        for (HttpMethod httpMethod : methodHttpMethods) {
            for (String controllerUrl : controllerUrls) {
                for (String methodUrl : methodUrls) {
                    methodIndicators.add(MethodIndicator.from(httpMethod, controllerUrl, methodUrl));
                }
            }
        }

        return new MethodResolver(methodIndicators, clazz, method);
    }

    public boolean isIndicated(MethodIndicator otherMethodIndicator) {
        return methodIndicators.stream()
            .anyMatch(methodIndicator -> methodIndicator.equals(otherMethodIndicator));
    }

    private static <T> T validateEmtpy(T value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException(MessageFormat.format("value is null. `type`/`value` = `{0}`/`{1}`", value.getClass().getSimpleName(), value));
        }

        if (value instanceof Collection<?> && ((Collection<?>) value).isEmpty()) {
            throw new RuntimeException(MessageFormat.format("value is empty. `type`/`value` = `{0}`/`{1}`", value.getClass().getSimpleName(), value));
        }

        return value;
    }
}
