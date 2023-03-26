package mapper;

import java.util.Optional;
import mapper.segment.UrlSegments;
import vo.HttpMethod;

/**
 * matcher 의 매치 여부에 따라 method 를 반환합니다.
 */
public interface MethodResolverIf {
    Optional<HttpPathResolver.MatchedMethod> resolve(HttpMethod httpMethod, UrlSegments requestSegments);
}
