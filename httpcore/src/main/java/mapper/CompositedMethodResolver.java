package mapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import mapper.segment.UrlSegments;
import vo.HttpMethod;

// n 개의 methodResolver 를 1개 처럼 다룬다.
public class CompositedMethodResolver implements MethodResolverIf {
    private final List<HttpPathMatcher> httpPathMatchers;

    public CompositedMethodResolver(List<HttpPathMatcher> httpPathMatchers) {
        if (Objects.isNull(httpPathMatchers)) {
            throw new RuntimeException("methodResolvers is null.");
        }

        List<HttpPathMatcher> newJavaMethodResolver = httpPathMatchers.stream()
            .filter(o -> !Objects.isNull(o))
            .collect(Collectors.toUnmodifiableList());

        if (newJavaMethodResolver.isEmpty()) {
            throw new RuntimeException("newMethodResovler is empty.");
        }

        this.httpPathMatchers = newJavaMethodResolver;
    }

    @Override
    public Optional<HttpPathMatcher.MatchedMethod> resolve(HttpMethod httpMethod, UrlSegments requestSegments) {
        return httpPathMatchers.stream()
            .map(methodResolver -> methodResolver.matchMethod(httpMethod, requestSegments))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }
}
