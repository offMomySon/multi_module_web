package matcher;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;

public class BaseHttpPathMatcher implements HttpPathMatcher {
    private final RequestMethod requestMethod;
    private final PathUrlMatcher baseUrlPathMatcher;
    private final Method javaMethod;

    public BaseHttpPathMatcher(RequestMethod requestMethod, PathUrlMatcher baseUrlPathMatcher, Method javaMethod) {
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(baseUrlPathMatcher);
        Objects.requireNonNull(javaMethod);
        this.requestMethod = requestMethod;
        this.baseUrlPathMatcher = baseUrlPathMatcher;
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

        Optional<PathVariableValue> optionalPathVariableValue = baseUrlPathMatcher.match(requestUrl);
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
}