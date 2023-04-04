package mapper;

import java.util.Optional;
import vo.RequestMethod;

/**
 * matcher 의 매치 여부에 따라 method 를 반환합니다.
 */
public interface HttpPathMatcherIf {
    Optional<HttpPathMatcher.MatchedMethod> matchMethod(RequestMethod requestMethod, String requestUrl);
}
