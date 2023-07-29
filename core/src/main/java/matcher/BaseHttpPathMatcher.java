package matcher;

import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;

// 생성자
// 1. requestMethod 를 받는다.
// 2. pathUrlMatcher 를 받는다.
// 3. java method 를 받는다.
// 4. 객체를 생성한다.
public class BaseHttpPathMatcher implements HttpPathMatcher {
    private final RequestMethod requestMethod;
    private final PathUrlMatcher pathUrlMatcher;
    private final Method javaMethod;

    public BaseHttpPathMatcher(RequestMethod requestMethod, PathUrlMatcher pathUrlMatcher, Method javaMethod) {
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(pathUrlMatcher);
        Objects.requireNonNull(javaMethod);
        this.requestMethod = requestMethod;
        this.pathUrlMatcher = pathUrlMatcher;
        this.javaMethod = javaMethod;
    }

    @Override
    public Optional<MatchedMethod> matchJavaMethod(RequestMethod requestMethod, PathUrl requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return Optional.empty();
        }
        if (this.requestMethod != requestMethod) {
            return Optional.empty();
        }

        Optional<PathVariableValue> optionalPathVariableValue = pathUrlMatcher.match(requestUrl);
        boolean doesNotMatch = optionalPathVariableValue.isEmpty();
        if (doesNotMatch) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = optionalPathVariableValue.get();
        return Optional.of(new MatchedMethod(javaMethod, pathVariableValue));
    }

    @Getter
    public static class MatchedMethod {
        private final Method javaMethod;
        private final PathVariableValue pathVariableValue;

        public MatchedMethod(Method javaMethod, PathVariableValue pathVariableValue) {
            Objects.requireNonNull(javaMethod);
            Objects.requireNonNull(pathVariableValue);
            this.javaMethod = javaMethod;
            this.pathVariableValue = pathVariableValue;
        }
    }

    @Override
    public String toString() {
        return "BaseHttpPathMatcher{" +
            "requestMethod=" + requestMethod +
            ", baseUrlPathMatcher=" + pathUrlMatcher +
            ", javaMethod=" + javaMethod +
            '}';
    }
}