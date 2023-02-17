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
    private final List<MethodResolver> methodResolvers;

    public MethodHandlerRepository(List<MethodResolver> methodResolvers) {
        if (Objects.isNull(methodResolvers)) {
            throw new RuntimeException("methodHandler is not exist.");
        }

        this.methodResolvers = methodResolvers.stream()
            .filter(methodResolver -> !Objects.isNull(methodResolvers))
            .collect(Collectors.toUnmodifiableList());
    }

    public static MethodHandlerRepository from(List<Class<?>> clazzes) {
        List<MethodResolver> methodResolvers = new ArrayList<>();
        for (Class<?> clazz : clazzes) {
            for (Method method : clazz.getMethods()) {
                if (!AnnotationUtils.find(method, RequestMapping.class).isPresent()) {
                    continue;
                }

                MethodResolver methodResolver = MethodResolver.from(clazz, method);
                methodResolvers.add(methodResolver);
            }
        }

        methodResolvers
            .forEach(methodResolver -> log.info("methodHandler : {}", methodResolver));

        return new MethodHandlerRepository(methodResolvers);
    }

    public Optional<MethodResolver> find(MethodIndicator indicator) {
        if (Objects.isNull(methodResolvers)) {
            return Optional.empty();
        }

        return methodResolvers.stream()
            .filter(methodResolver -> methodResolver.isIndicated(indicator))
            .findAny();
    }
}
