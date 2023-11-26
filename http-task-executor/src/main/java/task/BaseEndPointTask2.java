package task;

import java.util.Objects;
import java.util.Optional;
import matcher.MatchedEndPointTaskWorker2;
import matcher.PathUrlMatcher;
import matcher.RequestMethod;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import matcher.segment.factory.SegmentChunkFactory;
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

    public static BaseEndPointTask2 from(RequestMethod requestMethod, String url, EndPointTaskWorker2 endPointTaskWorker){
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(endPointTaskWorker);
        if(Objects.isNull(url) || url.isBlank()){
            throw new RuntimeException("Invalid parameter. url is empty.");
        }

        PathUrl pathUrl = PathUrl.from(url);
        SegmentChunkFactory segmentChunkFactory = new SegmentChunkFactory(pathUrl);
        PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(segmentChunkFactory);

        return new BaseEndPointTask2(requestMethod, pathUrlMatcher, endPointTaskWorker);
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
