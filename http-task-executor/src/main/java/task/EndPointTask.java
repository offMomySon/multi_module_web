package task;

import java.util.Optional;
import matcher.MatchedEndPointTaskWorker;
import matcher.RequestMethod;
import matcher.segment.PathUrl;

public interface EndPointTask {
    Optional<MatchedEndPointTaskWorker> match(RequestMethod requestMethod, PathUrl requestUrl);
}
