package mapper;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * matcher 의 매치 여부에 따라 method 를 반환합니다.
 */
public interface MethodResolver {
    Optional<Method> resolve(Matcher matcher);
}
