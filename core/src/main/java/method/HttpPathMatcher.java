package method;

import java.util.Optional;
import method.segment.PathUrl;
import web.RequestMethod;

/**
 * matcher 의 매치 여부에 따라 method 를 반환합니다.
 */
public interface HttpPathMatcher {
    Optional<BaseHttpPathMatcher.MatchedMethod> matchJavaMethod(RequestMethod requestMethod, PathUrl requestUrl);
}
