package mapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import vo.HttpMethod;
import static validate.ValidateUtil.validateNull;

@Slf4j
public class UrlMethodMapper {
    private final Map<MethodIndicator, Method> values;

    private UrlMethodMapper(Map<MethodIndicator, Method> values) {
        validateNull(values);

        this.values = createUnmodifiableUrlMethodMapper(values);
    }

    public Method findMethod(MethodIndicator methodIndicator) {
        return values.entrySet().stream()
            .filter(es -> es.getKey().isMatch(methodIndicator))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                MessageFormat.format("Does not exist, match url. given url : ", methodIndicator.getHttpUrl()))
            );
    }

    public static UrlMethodMapper create(Class _clazz, String packageName) {
        BufferedReader resourceReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                _clazz.getResourceAsStream("/" + packageName.replaceAll("[.]", "/")))
            )
        );

        List<ClassAnnotationDetector> allClasses = resourceReader.lines()
            .peek(l -> log.info("line : {}", l))
            .filter(isClass())
            .map(parseClassName())
            .map(generatePackageClassName(packageName))
            .map(UrlMethodMapper::getClass)
            .map(ClassAnnotationDetector::new)
            .collect(Collectors.toUnmodifiableList());

        Set<ClassAnnotationDetector> controllerDetector = allClasses.stream()
            .filter(a -> a.isAnnotatedOnClass(Controller.class))
            .collect(Collectors.toUnmodifiableSet());

        Map<MethodIndicator, Method> totalMethodMapper = new HashMap<>();
        for (ClassAnnotationDetector detector : controllerDetector) {
            Optional<Set<String>> optionalControllerUrls = detector.findAnnotationOnClass(RequestMapping.class)
                .map(RequestMapping::value)
                .map(Set::of);

            if (optionalControllerUrls.isEmpty()) {
                continue;
            }

            Set<String> controllerUrls = optionalControllerUrls.get();

            for (Method method : detector.findMethod(RequestMapping.class)) {
                Optional<RequestMapping> optionalRequestMappingMethod = detector.findAnnotationOnMethod(method, RequestMapping.class);

                if (optionalRequestMappingMethod.isEmpty()) {
                    continue;
                }

                RequestMapping requestMapping = optionalRequestMappingMethod.get();

                List<String> taskUrls = Arrays.stream(requestMapping.value()).collect(Collectors.toUnmodifiableList());
                List<HttpMethod> taskHttpMethods = Arrays.stream(requestMapping.method()).collect(Collectors.toUnmodifiableList());

                Set<MethodIndicator> methodIndicators = createMethodIndicator(taskUrls, taskHttpMethods);

                Set<MethodIndicator> fullUrlMethodIndicators = prevAppendUrlToMethodIndicators(controllerUrls, methodIndicators);

                Map<MethodIndicator, Method> methodMapper = fullUrlMethodIndicators.stream()
                    .map(methodIndicator -> Map.entry(methodIndicator, method))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (it1, it2) -> it1));

                totalMethodMapper.putAll(methodMapper);
            }
        }

        totalMethodMapper.forEach((key, value) -> log.info("key : {}, value : {}", key, value));

        return new UrlMethodMapper(totalMethodMapper);
    }

    private static Set<MethodIndicator> prevAppendUrlToMethodIndicators(Set<String> urls, Set<MethodIndicator> methodIndicators) {
        Set<MethodIndicator> newMethodIndicators = new HashSet<>();
        for (String url : urls) {
            for (MethodIndicator methodIndicator : methodIndicators) {
                newMethodIndicators.add(methodIndicator.prevAppendUrl(url));
            }
        }
        return Collections.unmodifiableSet(newMethodIndicators);
    }

    private static Set<MethodIndicator> createMethodIndicator(List<String> taskUrls, List<HttpMethod> taskHttpMethods) {
        Set<MethodIndicator> methodIndicators = new HashSet<>();
        for (String taskUrl : taskUrls) {
            for (HttpMethod httpMethod : taskHttpMethods) {
                methodIndicators.add(new MethodIndicator(taskUrl, httpMethod));
            }
        }

        return Collections.unmodifiableSet(methodIndicators);
    }

    private static Map<MethodIndicator, Method> createUnmodifiableUrlMethodMapper(Map<MethodIndicator, Method> values) {
        return values.entrySet().stream()
            .filter(Objects::nonNull)
            .filter(es -> Objects.nonNull(es.getValue()))
            .map(es -> Map.entry(es.getKey(), es.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (it, it1) -> it));
    }

    private static Function<String, String> generatePackageClassName(String packageName) {
        return s -> packageName + "." + s;
    }

    private static Function<String, String> parseClassName() {
        return className -> className.substring(0, className.lastIndexOf('.'));
    }

    private static Predicate<String> isClass() {
        return resource -> resource.endsWith(".class");
    }

    private static Class getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not found.");
        }
    }
}
