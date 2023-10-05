package matcher;

import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import task.HttpEndPointTask;
import task.endpoint.EndPointTask;
import task.endpoint.JavaMethodInvokeTask;

// 생성자
// 1. requestMethod 를 받는다.
// 2. pathUrlMatcher 를 받는다.
// 3. java method 를 받는다.
// 4. 객체를 생성한다.
public class JavaMethodEndpointTaskMatcher implements EndpointTaskMatcher {
    private final RequestMethod requestMethod;
    private final PathUrlMatcher pathUrlMatcher;
    private final HttpEndPointTask httpEndPointTask;

    public JavaMethodEndpointTaskMatcher(RequestMethod requestMethod, PathUrlMatcher pathUrlMatcher, HttpEndPointTask httpEndPointTask) {
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(pathUrlMatcher);
        Objects.requireNonNull(httpEndPointTask);
        this.requestMethod = requestMethod;
        this.pathUrlMatcher = pathUrlMatcher;
        this.httpEndPointTask = httpEndPointTask;
    }

    @Override
    public Optional<MatchedEndPoint> match(RequestMethod requestMethod, PathUrl requestUrl) {
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
        MatchedEndPoint matchedEndPoint = new MatchedEndPoint(httpEndPointTask, pathVariableValue);
        return Optional.of(matchedEndPoint);
    }
}