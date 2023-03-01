package mapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

// n 개의 methodResolver 를 1개 처럼 다룬다.
public class CompositedMethodResolver implements MethodResolver {
    private final List<MethodResolver> methodResolvers;

    public CompositedMethodResolver(List<MethodResolver> methodResolvers) {
        if (Objects.isNull(methodResolvers)) {
            throw new RuntimeException("methodResolvers is null.");
        }

        List<MethodResolver> newMethodResolver = methodResolvers.stream()
            .filter(o -> !Objects.isNull(o))
            .collect(Collectors.toUnmodifiableList());

        if (newMethodResolver.isEmpty()) {
            throw new RuntimeException("newMethodResovler is empty.");
        }

        this.methodResolvers = newMethodResolver;
    }

    @Override
    public Optional<Method> resolve(Matcher matcher) {
        return methodResolvers.stream()
            .map(methodResolver -> methodResolver.resolve(matcher))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }
}
