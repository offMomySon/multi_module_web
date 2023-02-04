package mapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;

@Slf4j
public class MethodHandlerRegister {
    private final List<MethodHandler> methodHandlers;

    public MethodHandlerRegister(List<MethodHandler> methodHandlers) {
        this.methodHandlers = methodHandlers;
    }

    public List<MethodHandler> getMethodHandlers() {
        return methodHandlers;
    }

    public static MethodHandlerRegister register(List<? extends Class<?>> classes) {
        List<Class<?>> controllerClazzs = classes.stream()
            .filter(aClass -> AnnotationUtils.find(aClass, Controller.class).isPresent())
            .collect(Collectors.toUnmodifiableList());

        List<MethodHandler> allMethodHandlers = new ArrayList<>();
        for (Class<?> clazz : controllerClazzs) {
            Set<String> controllerUrls = AnnotationUtils.find(clazz, RequestMapping.class)
                .map(RequestMapping::value)
                .map(Set::of)
                .orElseGet(() -> Set.of("/"));

            List<Method> methods = Arrays.stream(clazz.getMethods())
                .filter(method -> AnnotationUtils.find(method, RequestMapping.class).isPresent())
                .collect(Collectors.toUnmodifiableList());

            List<MethodHandler> methodHandlers = methods.stream()
                .map(method -> MethodHandler.from(controllerUrls, method))
                .collect(Collectors.toUnmodifiableList());

            allMethodHandlers.addAll(methodHandlers);
        }

        return new MethodHandlerRegister(allMethodHandlers);
    }
}
