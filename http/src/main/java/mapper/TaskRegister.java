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


        List<Class<?>> allClazz = bufferedReader.lines()
            .filter(isClassExtension())
            .map(parseClassName())
            .map(generatePackageClassName(packageName))
            .map(TaskRegister::getClass)
            .collect(Collectors.toUnmodifiableList());

        List<Class<?>> controllerClazzs = allClazz.stream()
            .filter(aClass -> AnnotationUtils.find(aClass, Controller.class).isPresent())
            .collect(Collectors.toUnmodifiableList());

        List<TaskActuator> taskActuators = new ArrayList<>();
        for (Class<?> controllerClazz : controllerClazzs) {
            Set<String> controllerUrls = AnnotationUtils.find(controllerClazz, RequestMapping.class)
                .map(RequestMapping::value)
                .map(Set::of)
                .orElseGet(() -> Set.of("/"));

            List<Method> methods = Arrays.stream(controllerClazz.getMethods())
                .filter(method -> AnnotationUtils.find(method, RequestMapping.class).isPresent())
                .collect(Collectors.toUnmodifiableList());

            for (Method method : methods) {
                RequestMapping requestMapping = AnnotationUtils.find(method, RequestMapping.class).orElseThrow(() -> new RuntimeException("request mapping 이 존재하지 않습니다."));

                Set<String> fullUrl = controllerUrls.stream()
                    .flatMap( controllerUrl -> Arrays.stream(requestMapping.value()).map(methodUrl -> controllerUrl + methodUrl))
                    .collect(Collectors.toUnmodifiableSet());

                Map<HttpMethod, Set<String>> methodAndUrls = Arrays.stream(requestMapping.method())
                    .map(_method -> Map.entry(_method, fullUrl))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (curr, prev) -> curr));


                List<TaskIndicator> taskIndicators = methodAndUrls.entrySet().stream()
                    .flatMap(entry -> entry.getValue().stream().map(url -> new TaskIndicator(entry.getKey(), url)))
                    .collect(Collectors.toUnmodifiableList());

                List<TaskActuator> methodTaskActuators = taskIndicators.stream()
                    .map(taskIndicator -> new TaskActuator(taskIndicator, method))
                    .collect(Collectors.toUnmodifiableList());

                taskActuators.addAll(methodTaskActuators);
            }
        }
        return new TaskRegister(taskActuators);
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
