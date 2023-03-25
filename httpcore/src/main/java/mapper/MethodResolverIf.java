package mapper;

import java.lang.reflect.Method;
import java.util.Optional;
import vo.HttpMethod;

/**
 * matcher 의 매치 여부에 따라 method 를 반환합니다.
 */
public interface MethodResolverIf {
    Optional<HttpPathResolver.ResolvedMethod> resolve(HttpMethod method, String url);
}
