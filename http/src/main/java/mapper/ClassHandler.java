package mapper;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import mapper.marker.RequestMapping;

public class ClassHandler {
    private final List<MethodHandler> methodHandlers;

    private ClassHandler(List<MethodHandler> _methodHandlers) {
        validateEmtpy(_methodHandlers);
        List<MethodHandler> methodHandlers = _methodHandlers.stream()
            .filter(methodHandler -> !Objects.isNull(methodHandler))
            .collect(Collectors.toUnmodifiableList());

        this.methodHandlers = methodHandlers;
    }

    public static ClassHandler from(Class<?> clazz) {
        validateEmtpy(clazz);

        Set<String> controllerUrls = AnnotationUtils.find(clazz, RequestMapping.class)
            .map(RequestMapping::value)
            .map(Set::of)
            .orElseGet(() -> Set.of("/"));

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
