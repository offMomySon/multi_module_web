package mapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

        Map<MethodIndicator, Method> urlMethodMapper = new HashMap<>();
        for (ClassAnnotationDetector detector : controllerDetector) {
            Optional<Set<String>> optionalControllerUrls = detector.findAnnotationOnClass(RequestMapping.class)
                .map(RequestMapping::value)
                .map(Set::of);

            if (optionalControllerUrls.isEmpty()) {
                continue;
            }

            Set<String> controllerUrls = optionalControllerUrls.get();

            for (Method method : detector.findAnnotatedMethods(RequestMapping.class)) {
                Set<String> httpUrl = getRequestUrl(method);
                Set<String> combinedUrl = combineUrls(controllerUrls, httpUrl);

                log.info("method : {}", method);
                Arrays.stream(method.getParameterAnnotations()).forEach(a -> log.info("param : {}", a));

                Set<HttpMethod> httpMethods = getHttpMethod(method);

                Set<MethodIndicator> methodIndicators = combinedUrl.stream()
                    .flatMap(url -> httpMethods.stream().map(hm -> new MethodIndicator(url, hm)))
                    .collect(Collectors.toUnmodifiableSet());

                for (MethodIndicator methodIndicator : methodIndicators) {
                    urlMethodMapper.put(methodIndicator, method);
                }
            }
        }

        urlMethodMapper.forEach((key, value) -> log.info("key : {}, value : {}", key, value));

        return new UrlMethodMapper(urlMethodMapper);
    }

//    /**
//     * read resource, at specified package.
//     *
//     * @param _clazz
//     * @param packageName
//     * @return
//     */
//    public static UrlMethodMapper create(Class _clazz, String packageName) {
//        BufferedReader resourceReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(_clazz.getResourceAsStream("/" + packageName.replaceAll("[.]", "/")))));
//
//        List<Class> allClasses = resourceReader.lines()
//            .peek(l -> log.info("line : {}", l))
//            .filter(isClass())
//            .map(parseClassName())
//            .map(generatePackageClassName(packageName))
//            .map(UrlMethodMapper::getClass)
//            .collect(Collectors.toUnmodifiableList());
//
//        Set<Class> controllerClasses = filterControllerClass(allClasses);
//
//        Map<MethodIndicator, Method> urlMethodMapper = new HashMap<>();
//        for (Class clazz : controllerClasses) {
//            Set<String> controllerUrls = getControllerUrls(clazz);
//
//            Set<Method> requestMappingMethods = filterRequestMappingMethod(clazz.getDeclaredMethods());
//            for (Method method : requestMappingMethods) {
//                Set<String> httpUrl = getRequestUrl(method);
//                Set<String> combinedUrl = combineUrls(controllerUrls, httpUrl);
//
//                log.info("method : {}", method);
//                Arrays.stream(method.getParameterAnnotations()).forEach(a -> log.info("param : {}", a));
//
//                Set<HttpMethod> httpMethods = getHttpMethod(method);
//
//                Set<MethodIndicator> methodIndicators = combinedUrl.stream()
//                    .flatMap(url -> httpMethods.stream().map(hm -> new MethodIndicator(url, hm)))
//                    .collect(Collectors.toUnmodifiableSet());
//
//                for (MethodIndicator methodIndicator : methodIndicators) {
//                    urlMethodMapper.put(methodIndicator, method);
//                }
//            }
//        }
//
//        urlMethodMapper.forEach((key, value) -> log.info("key : {}, value : {}", key, value));
//
//        return new UrlMethodMapper(urlMethodMapper);
//    }

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

    private static Set<Class> filterControllerClass(List<Class> allClasses) {
        return allClasses.stream()
            .filter(c -> Arrays.stream(c.getAnnotations()).anyMatch(a -> a instanceof Controller))
            .collect(Collectors.toUnmodifiableSet());
    }

    private static Set<String> getControllerUrls(Class clazz) {
        return Arrays.stream(clazz.getAnnotations())
            .filter(a -> a instanceof RequestMapping)
            .flatMap(a -> Arrays.stream(((RequestMapping) a).value()))
            .collect(Collectors.toUnmodifiableSet());
    }

    private static Set<Method> filterRequestMappingMethod(Method[] methods) {
        return Arrays.stream(methods)
            .filter(method -> Arrays.stream(method.getAnnotations()).anyMatch(a -> a instanceof RequestMapping))
            .collect(Collectors.toUnmodifiableSet());
    }

    private static Set<String> getRequestUrl(Method method) {
        return Arrays.stream(method.getAnnotations())
            .filter(a-> a.annotationType() == RequestMapping.class)
            .map(a -> (RequestMapping) a)
            .flatMap(r -> Stream.of(r.value()))
            .collect(Collectors.toUnmodifiableSet());
    }

    private static Set<String> combineUrls(Set<String> controllerUrls, Set<String> httpUrl) {
        return controllerUrls.stream()
            .flatMap(cu -> httpUrl.stream().map(hu -> cu + hu))
            .collect(Collectors.toUnmodifiableSet());
    }

    private static Set<HttpMethod> getHttpMethod(Method method) {
        return Arrays.stream(method.getAnnotations())
            .filter(a -> a instanceof RequestMapping)
            .map(a -> (RequestMapping) a)
            .flatMap(r -> Stream.of(r.method()))
            .collect(Collectors.toUnmodifiableSet());
    }
}
