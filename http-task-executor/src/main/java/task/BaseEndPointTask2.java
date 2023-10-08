package task;

import java.util.Objects;
import java.util.Optional;
import matcher.MatchedEndPointTaskWorker2;
import matcher.PathUrlMatcher;
import matcher.RequestMethod;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import task.worker.EndPointTaskWorker2;

public class BaseEndPointTask2 implements EndPointTask2 {
    private final RequestMethod requestMethod;
    private final PathUrlMatcher pathUrlMatcher;
    private final EndPointTaskWorker2 endPointTaskWorker;

    public BaseEndPointTask2(RequestMethod requestMethod, PathUrlMatcher pathUrlMatcher, EndPointTaskWorker2 endPointTaskWorker) {
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(pathUrlMatcher);
        Objects.requireNonNull(endPointTaskWorker);
        this.requestMethod = requestMethod;
        this.pathUrlMatcher = pathUrlMatcher;
        this.endPointTaskWorker = endPointTaskWorker;
    }

    @Override
    public Optional<MatchedEndPointTaskWorker2> match(RequestMethod requestMethod, PathUrl requestUrl) {
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
        MatchedEndPointTaskWorker2 matchedEndPoint = new MatchedEndPointTaskWorker2(endPointTaskWorker, pathVariableValue);
        return Optional.of(matchedEndPoint);
    }
}
