package task;

import java.util.Optional;
import matcher.MatchedEndPointTaskWorker2;
import matcher.RequestMethod;
import matcher.segment.PathUrl;

public interface EndPointTask2 {
    Optional<MatchedEndPointTaskWorker2> match(RequestMethod requestMethod, PathUrl requestUrl);
}