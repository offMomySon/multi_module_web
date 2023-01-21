package mapper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import vo.HttpMethod;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.validate;
import static validate.ValidateUtil.validateNull;

//    TODO file scan 으로 class 들을 찾으면 되지않나?
@Slf4j
public class MethodHandlerRegister {
    private final List<MethodHandler> methodHandlers;

    public MethodHandlerRegister(List<MethodHandler> methodHandlers) {
        this.methodHandlers = methodHandlers;
    }

    public List<MethodHandler> getTaskActuators() {
        return methodHandlers;
    }

    public static MethodHandlerRegister registerTaskMapper(Class<?> _clazz, String packageName) {
        validateNull(_clazz);
        validate(packageName);

        InputStream resourceInputStream = _clazz.getResourceAsStream(packageName);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceInputStream, 8192);
        InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8192);

        List<Class<?>> allClazz = bufferedReader.lines()
            .filter(isClassExtension())
            .map(parseClassName())
            .map(generatePackageClassName(packageName))
            .map(MethodHandlerRegister::getClass)
            .collect(Collectors.toUnmodifiableList());

        List<Class<?>> controllerClazzs = allClazz.stream()
            .filter(aClass -> AnnotationUtils.find(aClass, Controller.class).isPresent())
            .collect(Collectors.toUnmodifiableList());

        List<MethodHandler> actuators = new ArrayList<>();
        for (Class<?> clazz : controllerClazzs) {
            Set<String> controllerUrls = AnnotationUtils.find(clazz, RequestMapping.class)
                .map(RequestMapping::value)
                .map(Set::of)
                .orElseGet(()->Set.of("/"));

            List<Method> methods = Arrays.stream(clazz.getMethods())
                .filter(method -> AnnotationUtils.find(method, RequestMapping.class).isPresent())
                .collect(Collectors.toUnmodifiableList());

            for(Method method : methods){
                RequestMapping requestMapping = AnnotationUtils.find(method, RequestMapping.class)
                    .orElseThrow(()-> new RuntimeException("request mapping 이 존재하지 않습니다."));

                Set<HttpMethod> methodHttpMethods = Arrays.stream(requestMapping.method()).collect(Collectors.toUnmodifiableSet());
                Set<String> methodUrls = Arrays.stream(requestMapping.value()).collect(Collectors.toUnmodifiableSet());

                List<MethodIndicator> methodIndicators = new ArrayList<>();
                for (String controllerUrl : controllerUrls) {
                    for (String methodUrl : methodUrls) {
                        for(HttpMethod httpMethod : methodHttpMethods){
                            MethodIndicator methodIndicator = MethodIndicator.from(httpMethod, controllerUrl, methodUrl);
                            methodIndicators.add(methodIndicator);
                        }
                    }
                }

                MethodHandler methodHandlers = new MethodHandler(methodIndicators, method);
                actuators.add(methodHandlers);
            }
        }

        return new MethodHandlerRegister(actuators);
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
}
