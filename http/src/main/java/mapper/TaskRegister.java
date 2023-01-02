package mapper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
import static java.nio.charset.StandardCharsets.UTF_8;

//    TODO file scan 으로 class 들을 찾으면 되지않나?
@Slf4j
public class TaskRegister {
    private final List<TaskActuator> taskActuators;

    public TaskRegister(List<TaskActuator> taskActuators) {
        this.taskActuators = taskActuators;
    }

    public List<TaskActuator> getTaskActuators() {
        return taskActuators;
    }

    public static TaskRegister registerTaskMapper(Class<?> _clazz, String packageName) {
        ValidateUtil.validateNull(_clazz);
        ValidateUtil.validate(packageName);

        InputStream resourceInputStream = _clazz.getResourceAsStream(packageName);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceInputStream, 8192);
        InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8192);

        Map<Class<?>, Annotations> allClass = bufferedReader.lines()
            .filter(isClassExtension())
            .map(parseClassName())
            .map(generatePackageClassName(packageName))
            .map(TaskRegister::getClass)
            .collect(Collectors.toUnmodifiableMap(Function.identity(), Annotations::from, (curr, prev) -> curr));

        Map<Class<?>, Annotations> controllerClasses = allClass.entrySet().stream()
            .filter(entry -> entry.getValue().find(Controller.class).isPresent())
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (curr, prev) -> curr));

        List<TaskActuator> taskActuators = new ArrayList<>();
        for (Map.Entry<Class<?>, Annotations> controllerClass : controllerClasses.entrySet()) {
            Set<String> controllerUrls = controllerClass.getValue().find(RequestMapping.class)
                .map(RequestMapping::value)
                .map(Set::of)
                .orElseGet(() -> Set.of("/"));

            Map<Method, Annotations> allMethods = Arrays.stream(controllerClass.getKey().getMethods())
                .map(method -> Map.entry(method, Annotations.from(method)))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (curr, prev) -> curr));

            Map<Method, Annotations> requestMappingMethods = allMethods.entrySet().stream()
                .filter(entry -> entry.getValue().find(RequestMapping.class).isPresent())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (curr, prev) -> curr));

            for (Map.Entry<Method, Annotations> requestMappingMethodEntry : requestMappingMethods.entrySet()) {
                RequestMapping requestMapping = requestMappingMethodEntry.getValue().find(RequestMapping.class)
                    .orElseThrow(()-> new RuntimeException("not exist requestMapping"));

                Set<String> fullUrls = controllerUrls.stream()
                    .flatMap(controllerUrl -> Arrays.stream(requestMapping.value()).map( methodUrl-> controllerUrl + methodUrl) )
                    .collect(Collectors.toUnmodifiableSet());

                Map<HttpMethod, Set<String>> methodFullUrls = Arrays.stream(requestMapping.method())
                    .map(method -> Map.entry(method, fullUrls))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (curr, prev) -> curr));

                List<TaskIndicator> taskIndicators = methodFullUrls.entrySet().stream()
                    .flatMap(entry -> entry.getValue().stream().map(url -> new TaskIndicator(entry.getKey(), url)))
                    .collect(Collectors.toUnmodifiableList());

                List<TaskActuator> methodTaskActuators = taskIndicators.stream()
                    .map(taskIndicator -> new TaskActuator(taskIndicator, requestMappingMethodEntry.getKey()))
                    .collect(Collectors.toUnmodifiableList());

                taskActuators.addAll(methodTaskActuators);
            }
        }

        return null;
    }

    private static Function<String, String> generatePackageClassName(String packageName) {
        return s -> packageName + "." + s;
    }

    private static Function<String, String> parseClassName() {
        return className -> className.substring(0, className.lastIndexOf('.'));
    }

    private static Predicate<String> isClassExtension() {
        return resource -> resource.endsWith(".class");
    }

    private static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not found.");
        }
    }

    private static boolean isContainAny(Class<?> clazz, Class<?>... annotationClazzs) {
        Annotations classAnnotations = Annotations.from(clazz);

        return Arrays.stream(annotationClazzs)
            .map(classAnnotations::find)
            .anyMatch(Optional::isPresent);
    }
}
