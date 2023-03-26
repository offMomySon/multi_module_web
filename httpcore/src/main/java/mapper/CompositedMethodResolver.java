package mapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import mapper.segment.UrlSegments;
import vo.HttpMethod;

// n 개의 methodResolver 를 1개 처럼 다룬다.
public class CompositedMethodResolver implements MethodResolverIf {
    private final List<HttpPathResolver> httpPathResolvers;

    public CompositedMethodResolver(List<HttpPathResolver> httpPathResolvers) {
        if (Objects.isNull(httpPathResolvers)) {
            throw new RuntimeException("methodResolvers is null.");
        }

        List<HttpPathResolver> newJavaMethodResolver = httpPathResolvers.stream()
            .filter(o -> !Objects.isNull(o))
            .collect(Collectors.toUnmodifiableList());

        if (newJavaMethodResolver.isEmpty()) {
            throw new RuntimeException("newMethodResovler is empty.");
        }

        this.httpPathResolvers = newJavaMethodResolver;
    }

    @Override
    public Optional<HttpPathResolver.MatchedMethod> resolve(HttpMethod httpMethod, UrlSegments requestSegments) {
        return httpPathResolvers.stream()
            .map(methodResolver -> methodResolver.resolveMethod(httpMethod, requestSegments))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }
}
