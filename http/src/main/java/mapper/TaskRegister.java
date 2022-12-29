package mapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

        List<ElementAnnotationDetector> classAnnotationDetectors = resourceReader.lines()
            .peek(l -> log.info("line : {}", l))
            .filter(isClass())
            .map(parseClassName())
            .map(generatePackageClassName(packageName))
            .map(TaskRegister::getClass)
            .map(AnnotatedClass::new)
            .map(ElementAnnotationDetector::new)
            .collect(Collectors.toUnmodifiableList());

        Set<ElementAnnotationDetector> controllerAnnotatedDetector = classAnnotationDetectors.stream()
            .filter(elementAnnotationDetector -> elementAnnotationDetector.isAnnotated(Controller.class))
            .collect(Collectors.toUnmodifiableSet());

        Map<TaskIndicator, Method> totalMethodMapper = new HashMap<>();
        for (ElementAnnotationDetector detector : controllerAnnotatedDetector) {
            if (detector.doesNotHasSubElement()) {
                continue;
            }
            if (detector.doesNotAnnotated(RequestMapping.class)) {
                continue;
            }

            Optional<Set<String>> optionalControllerUrls = detector.find(RequestMapping.class)
                .map(RequestMapping::value)
                .map(Set::of);

            if (optionalControllerUrls.isEmpty()) {
                continue;
            }

            Set<String> controllerUrls = optionalControllerUrls.get();

            for (AnnotatedElement annotatedMethod : detector.findAnnotatedElementOnSubElement(RequestMapping.class)) {
                ElementAnnotationDetector methodAnnotationDetector = new ElementAnnotationDetector(annotatedMethod);

                Optional<RequestMapping> optionalRequestMapping = methodAnnotationDetector.find(RequestMapping.class);

                if (optionalRequestMapping.isEmpty()) {
                    continue;
                }

                RequestMapping requestMapping = optionalRequestMapping.get();

                List<String> fullTaskUrls = new ArrayList<>();
                for (String taskUrl : Arrays.stream(requestMapping.value()).collect(Collectors.toUnmodifiableList())) {
                    for (String controllerUrl : controllerUrls) {
                        fullTaskUrls.add(controllerUrl + taskUrl);
                    }
                }
                fullTaskUrls = Collections.unmodifiableList(fullTaskUrls);

                Map<TaskIndicator, Method> methodMapper = new HashMap<>();
                for (HttpMethod methodHttpMethod : Arrays.stream(requestMapping.method()).collect(Collectors.toUnmodifiableList())) {
                    for (String fullTaskUrl : fullTaskUrls) {
                        methodMapper.put(new TaskIndicator(fullTaskUrl, methodHttpMethod), ((AnnotatedMethod) annotatedMethod).getMethod());
                    }
                }
                methodMapper = Collections.unmodifiableMap(methodMapper);

                totalMethodMapper.putAll(methodMapper);
            }
        }

        totalMethodMapper.forEach((key, value) -> log.info("key : {}, value : {}", key, value));

        TaskMapper TaskRegister = new TaskMapper(totalMethodMapper);

        return new TaskRegister(TaskRegister);
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
