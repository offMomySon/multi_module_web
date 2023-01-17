package mapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

public class MethodHandlerCreator {
    private final Class<?> clazz;

    public MethodHandlerCreator(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            throw new RuntimeException("clazz is null.");
        }
        this.clazz = clazz;
    }

    public List<MethodHandler> generateTaskActuator() {
        Set<String> controllerUrls = getControllerUrls(this.clazz);

        List<Method> methods = getAnnotatedMethod(this.clazz.getMethods(), RequestMapping.class);

        List<MethodHandler> actuators = new ArrayList<>();
        for (Method method : methods) {


            Map<HttpMethod, Set<String>> methodHttpMethodAndUrls = getHttpMethodAndUrls(method);
            Map<HttpMethod, Set<String>> methodHttpMethodAndFullUrls = prependUrls(controllerUrls, methodHttpMethodAndUrls);

            List<MethodIndicator> methodIndicators = createTaskIndicators(methodHttpMethodAndFullUrls);
            List<MethodHandler> methodHandlers = createTaskActuators(method, methodIndicators);

            actuators.addAll(methodHandlers);
        }

        return Collections.unmodifiableList(actuators);
    }

    private List<Method> getAnnotatedMethod(Method[] methods, Class<?> annotationClazz) {
        return Arrays.stream(methods)
            .filter(method -> AnnotationUtils.find(method, annotationClazz).isPresent())
            .collect(Collectors.toUnmodifiableList());
    }

    private Set<String> getControllerUrls(Class<?> clazz) {
        return AnnotationUtils.find(clazz, RequestMapping.class)
            .map(RequestMapping::value)
            .map(Set::of)
            .orElseGet(() -> Set.of("/"));
    }

    private static List<MethodHandler> createTaskActuators(Method method, List<MethodIndicator> methodIndicators) {
        return methodIndicators.stream()
            .map(taskIndicator -> new MethodHandler(taskIndicator, method))
            .collect(Collectors.toUnmodifiableList());
    }

    private static List<MethodIndicator> createTaskIndicators(Map<HttpMethod, Set<String>> methodHttpMethodAndFullUrls) {
        return methodHttpMethodAndFullUrls.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream().map(url -> new MethodIndicator(entry.getKey(), url)))
            .collect(Collectors.toUnmodifiableList());
    }

    private static Map<HttpMethod, Set<String>> prependUrls(Set<String> controllerUrls, Map<HttpMethod, Set<String>> methodHttpMethodAndUrls) {
        Map<HttpMethod, Set<String>> newMethodHttpMethodAndUrls = new HashMap<>();
        for (Map.Entry<HttpMethod, Set<String>> entry : methodHttpMethodAndUrls.entrySet()) {
            HttpMethod httpMethod = entry.getKey();
            Set<String> fullUrl = entry.getValue().stream()
                .flatMap(methodUrl -> controllerUrls.stream().map(controllerUrl -> controllerUrl + methodUrl))
                .collect(Collectors.toUnmodifiableSet());

            newMethodHttpMethodAndUrls.put(httpMethod, fullUrl);
        }
        return Collections.unmodifiableMap(newMethodHttpMethodAndUrls);
    }

    private static Map<HttpMethod, Set<String>> getHttpMethodAndUrls(Method method) {
        RequestMapping requestMapping = AnnotationUtils.find(method, RequestMapping.class)
            .orElseThrow(() -> new RuntimeException("requestMapping 이 존재하지 않습니다."));

        String[] urls = requestMapping.value();
        HttpMethod[] httpMethods = requestMapping.method();

        return Arrays.stream(httpMethods)
            .map(httpMethod -> Map.entry(httpMethod, Set.of(urls)))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (curr, prev) -> curr));
    }
}
