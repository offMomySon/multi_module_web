package matcher;

import matcher.segment.PathUrl;
import java.util.Optional;

/**
 * matcher 의 매치 여부에 따라 method 를 반환합니다.
 */
public interface EndpointMatcher {
//    Optional<MatchedMethod> match(RequestMethod requestMethod, PathUrl requestUrl);

    Optional<MatchedEndPoint> match(RequestMethod requestMethod, PathUrl requestUrl);
}
