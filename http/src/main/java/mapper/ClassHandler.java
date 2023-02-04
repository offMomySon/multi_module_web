package mapper;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;

@ToString
@Slf4j
public class ClassHandler {
    private final List<MethodHandler> methodHandlers;

    private ClassHandler(List<MethodHandler> methodHandlers) {
        validateEmtpy(methodHandlers);

        this.methodHandlers = methodHandlers.stream()
            .filter(methodHandler -> !Objects.isNull(methodHandler))
            .collect(Collectors.toUnmodifiableList());
    }

    public static ClassHandler from(Class<?> clazz) {
        validateEmtpy(clazz);
        AnnotationUtils.find(clazz, Controller.class)
            .orElseThrow(() -> new RuntimeException("Controller annotation does not exist."));

        RequestMapping requestMapping = AnnotationUtils.find(clazz, RequestMapping.class)
            .orElseThrow(() -> new RuntimeException("RequestMapping annotation does not exist."));

        Set<String> controllerUrls = Arrays.stream(requestMapping.value())
            .filter(controllerUrl -> !Objects.isNull(controllerUrl))
            .collect(Collectors.toUnmodifiableSet());
        List<Method> requestMappingMethods = Arrays.stream(clazz.getMethods())
            .filter(method -> AnnotationUtils.find(method, RequestMapping.class).isPresent())
            .collect(Collectors.toUnmodifiableList());

        List<MethodHandler> methodHandlers = requestMappingMethods.stream()
            .map(method -> MethodHandler.from(controllerUrls, method))
            .collect(Collectors.toUnmodifiableList());

        return new ClassHandler(methodHandlers);
    }

    private static <T> T validateEmtpy(T value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException(MessageFormat.format("value is null. `type`/`value` = `{0}`/`{1}`", value.getClass().getSimpleName(), value));
        }
        return value;
    }
}
