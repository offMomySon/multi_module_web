package matcher;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import matcher.segment.PathUrl;

// http
// link
// context

// n 개의 methodResolver 를 1개 처럼 다룬다.
public class CompositedEndpointMatcher implements EndpointMatcher {
    private final List<EndpointMatcher> baseHttpPathMatchers;

    public CompositedEndpointMatcher(List<EndpointMatcher> baseHttpPathMatchers) {
        if (Objects.isNull(baseHttpPathMatchers)) {
            throw new RuntimeException("methodResolvers is null.");
        }

        List<EndpointMatcher> newJavaMethodResolver = baseHttpPathMatchers.stream()
            .filter(o -> !Objects.isNull(o))
            .collect(Collectors.toUnmodifiableList());

        if (newJavaMethodResolver.isEmpty()) {
            throw new RuntimeException("newMethodResovler is empty.");
        }

        this.baseHttpPathMatchers = newJavaMethodResolver;
    }

//    1. method, request url 이 매칭되는 pathMatcher 가 존재하면 method, pathVariable value 를 반환합니다.
    public Optional<MatchedHttpTask> match(RequestMethod requestMethod, PathUrl requestUrl) {
        return baseHttpPathMatchers.stream()
            .map(methodResolver -> methodResolver.match(requestMethod, requestUrl))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }
}