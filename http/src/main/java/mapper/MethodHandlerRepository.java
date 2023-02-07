package mapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import mapper.marker.RequestMapping;

/**
 * 역할
 * methodHandler 를 저장하고 조회하는 역할.
 */
@ToString
@Slf4j
public class MethodHandlerRepository {
    private final List<MethodHandler> methodHandlers;

    public MethodHandlerRepository(List<MethodHandler> methodHandlers) {
        if (Objects.isNull(methodHandlers)) {
            throw new RuntimeException("methodHandler is not exist.");
        }

        this.methodHandlers = methodHandlers.stream()
            .filter(methodHandler -> !Objects.isNull(methodHandlers))
            .collect(Collectors.toUnmodifiableList());
    }

    public static MethodHandlerRepository from(List<Class<?>> clazzes) {
        List<MethodHandler> methodHandlers = new ArrayList<>();
        for (Class<?> clazz : clazzes) {
            for (Method method : clazz.getMethods()) {
                if (!AnnotationUtils.find(method, RequestMapping.class).isPresent()) {
                    continue;
                }

                MethodHandler methodHandler = MethodHandler.from(clazz, method);
                methodHandlers.add(methodHandler);
            }
        }

        methodHandlers
            .forEach(methodHandler -> log.info("methodHandler : {}", methodHandler));

        return new MethodHandlerRepository(methodHandlers);
    }

    public Optional<MethodHandler> find(MethodIndicator indicator) {
        if (Objects.isNull(methodHandlers)) {
            return Optional.empty();
        }

        return methodHandlers.stream()
            .filter(methodHandler -> methodHandler.isIndicated(indicator))
            .findAny();
    }
}
