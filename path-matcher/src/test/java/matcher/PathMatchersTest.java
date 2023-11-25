package matcher;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import matcher.PathMatchers.MatchedElement;
import matcher.path.PathUrl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathMatchersTest {
    @DisplayName("일치하는 path 가 존재하면 매치된 값을 가져온다.")
    @Test
    void test() throws Exception {
        //given
        PathUrl pathUrl = PathUrl.of("/GET/test");
        PathUrl requestPathUrl = PathUrl.of("/GET/test");
        PathMatcher pathMatcher = PathMatcher.of(pathUrl);
        Method method = TestClass.class.getDeclaredMethod("method");

        Map<PathMatcher, Method> map = Map.of(pathMatcher, method);
        PathMatchers<Method> methodPathMatchers = new PathMatchers<Method>(map);

        //when
        Optional<MatchedElement<Method>> optionalActual = methodPathMatchers.match(requestPathUrl);

        //then
        Assertions.assertThat(optionalActual).isPresent();
        MatchedElement<Method> methodMatchedElement = optionalActual.get();
        Method actual = methodMatchedElement.getElement();
        Assertions.assertThat(actual).isEqualTo(method);
    }

    @DisplayName("일치하는 path 가 존재하지 않으면 빈값을 가져온다.")
    @Test
    void test1() throws Exception {
        //given
        PathUrl pathUrl = PathUrl.of("/GET/test");
        PathUrl requestPathUrl = PathUrl.of("/GET/test/depth1");
        PathMatcher pathMatcher = PathMatcher.of(pathUrl);
        Method method = TestClass.class.getDeclaredMethod("method");

        Map<PathMatcher, Method> map = Map.of(pathMatcher, method);
        PathMatchers<Method> methodPathMatchers = new PathMatchers<Method>(map);

        //when
        Optional<MatchedElement<Method>> optionalActual = methodPathMatchers.match(requestPathUrl);

        //then
        Assertions.assertThat(optionalActual).isEmpty();
    }

    public static class TestClass{
        public void method(){

        }
    }
}