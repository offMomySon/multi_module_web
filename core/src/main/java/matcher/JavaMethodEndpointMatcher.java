package matcher;

import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import task.HttpTask;
import task.JavaMethodTask;

// 생성자
// 1. requestMethod 를 받는다.
// 2. pathUrlMatcher 를 받는다.
// 3. java method 를 받는다.
// 4. 객체를 생성한다.
public class JavaMethodEndpointMatcher implements EndpointMatcher {
    private final RequestMethod requestMethod;
    private final PathUrlMatcher pathUrlMatcher;
    private final Object declaringInstance;
    private final Method javaMethod;

    public JavaMethodEndpointMatcher(RequestMethod requestMethod, PathUrlMatcher pathUrlMatcher, Object declaringInstance, Method javaMethod) {
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(pathUrlMatcher);
        Objects.requireNonNull(declaringInstance);
        Objects.requireNonNull(javaMethod);
        this.requestMethod = requestMethod;
        this.pathUrlMatcher = pathUrlMatcher;
        this.declaringInstance = declaringInstance;
        this.javaMethod = javaMethod;
    }

    @Override
    public Optional<MatchedHttpTask> match(RequestMethod requestMethod, PathUrl requestUrl) {
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

        HttpTask httpTask = new JavaMethodTask(declaringInstance, javaMethod);
        PathVariableValue pathVariableValue = optionalPathVariableValue.get();
        MatchedHttpTask matchedHttpTask = new MatchedHttpTask(httpTask, pathVariableValue);
        return Optional.of(matchedHttpTask);
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