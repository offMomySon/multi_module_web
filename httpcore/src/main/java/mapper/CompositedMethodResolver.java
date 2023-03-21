package mapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

// n 개의 methodResolver 를 1개 처럼 다룬다.
public class CompositedMethodResolver implements MethodResolverIf {
    private final List<JavaMethodResolver> javaMethodResolvers;

    public CompositedMethodResolver(List<JavaMethodResolver> javaMethodResolvers) {
        if (Objects.isNull(javaMethodResolvers)) {
            throw new RuntimeException("methodResolvers is null.");
        }

        List<JavaMethodResolver> newJavaMethodResolver = javaMethodResolvers.stream()
            .filter(o -> !Objects.isNull(o))
            .collect(Collectors.toUnmodifiableList());

        if (newJavaMethodResolver.isEmpty()) {
            throw new RuntimeException("newMethodResovler is empty.");
        }

        this.javaMethodResolvers = newJavaMethodResolver;
    }

    @Override
    public Optional<Method> resolve(Matcher matcher) {
        return javaMethodResolvers.stream()
            .map(methodResolver -> methodResolver.resolve(matcher))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }
}
