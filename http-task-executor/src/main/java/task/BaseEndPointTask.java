package task;

import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import matcher.MatchedEndPointTaskWorker;
import matcher.PathUrlMatcher;
import matcher.RequestMethod;
import matcher.segment.PathUrl2;
import matcher.segment.PathVariableValue;
import task.worker.EndPointTaskWorker;

@Getter
public class BaseEndPointTask implements EndPointTask{
    private final RequestMethod requestMethod;
    private final PathUrlMatcher pathUrlMatcher;
    private final EndPointTaskWorker endPointTaskWorker;

    public BaseEndPointTask(RequestMethod requestMethod, PathUrlMatcher pathUrlMatcher, EndPointTaskWorker endPointTaskWorker) {
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(pathUrlMatcher);
        Objects.requireNonNull(endPointTaskWorker);
        this.requestMethod = requestMethod;
        this.pathUrlMatcher = pathUrlMatcher;
        this.endPointTaskWorker = endPointTaskWorker;
    }

    @Override
    public Optional<MatchedEndPointTaskWorker> match(RequestMethod requestMethod, PathUrl2 requestUrl) {
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
        MatchedEndPointTaskWorker matchedEndPoint = new MatchedEndPointTaskWorker(endPointTaskWorker, pathVariableValue);
        return Optional.of(matchedEndPoint);
    }
}
