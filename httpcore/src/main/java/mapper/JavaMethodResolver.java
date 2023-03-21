package mapper;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import lombok.ToString;

/**
 * 역할.
 * HttpMethodUrlMatcher 의 매칭여부에 따라 method 를 반환합니다.
 */
@ToString
public class JavaMethodResolver implements MethodResolverIf {
    private final Method method;
    private final HttpMethodUrlMatcher httpMethodUrlMatcher;

    public JavaMethodResolver(Method method, HttpMethodUrlMatcher httpMethodUrlMatcher) {
        if (Objects.isNull(method) || Objects.isNull(httpMethodUrlMatcher)) {
            throw new RuntimeException("parameter is null.");
        }

        this.method = method;
        this.httpMethodUrlMatcher = httpMethodUrlMatcher;
    }

    @Override
    public Optional<Method> resolve(Matcher matcher) {
        boolean doesNotMatch = httpMethodUrlMatcher.doesNotMatch(matcher);
        if (doesNotMatch) {
            return Optional.empty();
        }

        return Optional.of(method);
    }
}
