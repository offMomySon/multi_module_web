package com.main.matcher;

import com.main.matcher.segment.PathUrl;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.main.matcher.BaseHttpPathMatcher.MatchedMethod;

// http
// link
// context

// n 개의 methodResolver 를 1개 처럼 다룬다.
public class CompositedHttpPathMatcher implements HttpPathMatcher {
    private final List<BaseHttpPathMatcher> baseHttpPathMatchers;

    public CompositedHttpPathMatcher(List<BaseHttpPathMatcher> baseHttpPathMatchers) {
        if (Objects.isNull(baseHttpPathMatchers)) {
            throw new RuntimeException("methodResolvers is null.");
        }

        List<BaseHttpPathMatcher> newJavaMethodResolver = baseHttpPathMatchers.stream()
            .filter(o -> !Objects.isNull(o))
            .collect(Collectors.toUnmodifiableList());

        if (newJavaMethodResolver.isEmpty()) {
            throw new RuntimeException("newMethodResovler is empty.");
        }

        this.baseHttpPathMatchers = newJavaMethodResolver;
    }

    public Optional<MatchedMethod> matchJavaMethod(RequestMethod requestMethod, PathUrl requestUrl) {
        return baseHttpPathMatchers.stream()
            .map(methodResolver -> methodResolver.matchJavaMethod(requestMethod, requestUrl))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }
}
