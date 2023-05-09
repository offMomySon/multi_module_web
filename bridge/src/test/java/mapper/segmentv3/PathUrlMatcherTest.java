package mapper.segmentv3;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import mapper.segmentv3.PathMatchTestSuite.PathMatchTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PathUrlMatcherTest {

    @DisplayName("methodPath 와 requestUrl 가 매칭되면 pathvariable 을 가져옵니다.")
    @PathMatchTest
    void test1(String baseUrl, String requestUrl, boolean expect) throws Exception {
        //given
        PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(PathUrl.from(baseUrl));
        //whenD
        Optional<PathVariableValue> optionalActual = pathUrlMatcher.match(PathUrl.from(requestUrl));

        //then
        Assertions.assertThat(optionalActual.isPresent()).isEqualTo(expect);
    }

    @DisplayName("처음 일치한 pathVariable 을 찾아온다.")
    @ParameterizedTest
    @MethodSource("providePathVariable")
    void test(String _baseUrl, String _requestUrl, PathVariableValue expect) throws Exception {
        //given
        PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(PathUrl.from(_baseUrl));
        //when
        Optional<PathVariableValue> optionalActual = pathUrlMatcher.match(PathUrl.from(_requestUrl));

        //then
        Assertions.assertThat(optionalActual).isPresent();
        PathVariableValue actual = optionalActual.get();
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    public static Stream<Arguments> providePathVariable() {
        return Stream.of(
            Arguments.of("/{pv1}/{pv2}/{pv3}", "/path1/path2/path3", new PathVariableValue(Map.of("pv1", "path1", "pv2", "path2", "pv3", "path3"))),
            Arguments.of("/{pv1}/path1/{pv2}", "/path1/path1/path2", new PathVariableValue(Map.of("pv1", "path1", "pv2", "path2"))),
            Arguments.of("/**/{pv1}", "/path1/path2/path3", new PathVariableValue(Map.of("pv1", "path3"))),
            Arguments.of("/**/{pv1}/**/{pv2}/**/{pv3}", "/path1/path2/path3/path4/path5/path6", new PathVariableValue(Map.of("pv1", "path1", "pv2", "path2", "pv3", "path6"))),
            Arguments.of("/**/{pv1}/path1", "/path1/path1/path1", new PathVariableValue(Map.of("pv1", "path1"))),
            Arguments.of("/path1/**/{pv1}", "/path1/path1/path2", new PathVariableValue(Map.of("pv1", "path2"))),
            Arguments.of("/path1/**/{pv1}/**/{pv2}", "/path1/path2/path3/path4/path5/path6", new PathVariableValue(Map.of("pv1", "path2", "pv2", "path6"))),
            Arguments.of("/path1/**/{pv1}/path1", "/path1/path2/path1/path2/path1", new PathVariableValue(Map.of("pv1", "path2"))),
            Arguments.of("/path1/**/path1/{pv1}", "/path1/path1/path3/path1/path1", new PathVariableValue(Map.of("pv1", "path1"))),
            Arguments.of("/path1/**/path1/{pv1}/**/{pv2}/path1", "/path1/path2/path2/path1/path3/path2/path1", new PathVariableValue(Map.of("pv1", "path3", "pv2", "path2"))),
            Arguments.of("/path1/**/path1/**/{pv1}/**/{pv2}/path1", "/path1/path2/path1/path3/path5/path1", new PathVariableValue(Map.of("pv1", "path3", "pv2", "path5"))),
            Arguments.of("/path1/{pv1}/**/path1/**/{pv2}/**/{pv3}/path1", "/path1/path2/path1/path3/path5/path1", new PathVariableValue(Map.of("pv1", "path2", "pv2", "path3", "pv3", "path5"))),
            Arguments.of("/{pv1}/**/path1", "/path4/path1", new PathVariableValue(Map.of("pv1", "path4")))
        );
    }
}