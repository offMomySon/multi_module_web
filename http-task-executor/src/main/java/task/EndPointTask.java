package task;

import java.util.Objects;
import java.util.Optional;
import matcher.MatchedEndPoint2;
import matcher.RequestMethod;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;

public interface EndPointTask {
    Optional<MatchedEndPoint2> match(RequestMethod requestMethod, PathUrl requestUrl);
}
