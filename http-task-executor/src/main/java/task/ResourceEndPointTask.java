package task;

import java.util.Optional;
import matcher.MatchedEndPoint2;
import matcher.RequestMethod;
import matcher.segment.PathUrl;

public class ResourceEndPointTask implements EndPointTask{
    @Override
    public Optional<MatchedEndPoint2> match(RequestMethod requestMethod, PathUrl requestUrl) {
        return Optional.empty();
    }
}
