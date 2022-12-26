package mapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import validate.ValidateUtil;
import vo.HttpMethod;

//    TODO file scan 으로 class 들을 찾으면 되지않나?
@Slf4j
public class TaskRegister {
    private final TaskMapper taskMapper;

    private TaskRegister(TaskMapper taskMapper) {
        this.taskMapper = ValidateUtil.validateNull(taskMapper);
    }

    public TaskMapper getTaskMapper() {
        return taskMapper;
    }

    public static TaskRegister registerTaskMapper(Class<?> _clazz, String packageName) {
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
            .map(TaskRegister::getClass)
            .map(ClassAnnotationDetector::new)
            .collect(Collectors.toUnmodifiableList());

        Set<ClassAnnotationDetector> controllerDetector = allClasses.stream()
            .filter(a -> a.isAnnotatedOnClass(Controller.class))
            .collect(Collectors.toUnmodifiableSet());

        Map<TaskIndicator, Method> totalMethodMapper = new HashMap<>();
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

                Set<TaskIndicator> taskIndicators = createMethodIndicator(taskUrls, taskHttpMethods);

                Set<TaskIndicator> fullUrlTaskIndicators = prevAppendUrlToMethodIndicators(controllerUrls, taskIndicators);

                Map<TaskIndicator, Method> methodMapper = fullUrlTaskIndicators.stream()
                    .map(taskIndicator -> Map.entry(taskIndicator, method))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (it1, it2) -> it1));

                totalMethodMapper.putAll(methodMapper);
            }
        }

        totalMethodMapper.forEach((key, value) -> log.info("key : {}, value : {}", key, value));

        TaskMapper TaskRegister = new TaskMapper(totalMethodMapper);

        return new TaskRegister(TaskRegister);
    }

    private static Set<TaskIndicator> prevAppendUrlToMethodIndicators(Set<String> urls, Set<TaskIndicator> taskIndicators) {
        Set<TaskIndicator> newTaskIndicators = new HashSet<>();
        for (String url : urls) {
            for (TaskIndicator taskIndicator : taskIndicators) {
                newTaskIndicators.add(taskIndicator.prevAppendUrl(url));
            }
        }
        return Collections.unmodifiableSet(newTaskIndicators);
    }

    private static Set<TaskIndicator> createMethodIndicator(List<String> taskUrls, List<HttpMethod> taskHttpMethods) {
        Set<TaskIndicator> taskIndicators = new HashSet<>();
        for (String taskUrl : taskUrls) {
            for (HttpMethod httpMethod : taskHttpMethods) {
                taskIndicators.add(new TaskIndicator(taskUrl, httpMethod));
            }
        }

        return Collections.unmodifiableSet(taskIndicators);
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

    private static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not found.");
        }
    }
}
