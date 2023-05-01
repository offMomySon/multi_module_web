package mapper;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import mapper.newsegment.SegmentManager;
import marker.RequestMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vo.RequestValues;

class HttpPathMatcherTest {

    @DisplayName("http path 와 일치하면 resolve 데이터를 가져옵니다.")
    @ParameterizedTest
    @MethodSource("mapper.TestSuitUtils#provideUrlTestSuite")
    void test1(String registerPath, String requestPath, boolean expect) throws Exception {
        //given
        HttpPathMatcher httpPathMatcher = new HttpPathMatcher(RequestMethod.GET, registerPath, TestClass.class.getDeclaredMethod("method"));

        //when
        boolean actual = httpPathMatcher.matchMethod(RequestMethod.GET, requestPath).isPresent();

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("")
    @ParameterizedTest
    @MethodSource("mapper.TestSuitUtils#provideUrlTestSuite")
    void test3(String methodPath, String requestPath, boolean expect) throws Exception {
        //given
        //when
        Optional<RequestValues> optionalActual = SegmentManager.doMatch(methodPath, requestPath);

        //then
        Assertions.assertThat(optionalActual.isPresent()).isEqualTo(expect);
    }

    @DisplayName("pathVariable 로 부터 값을 가져옵니다.")
    @ParameterizedTest
    @MethodSource("provideResult")
    void test1(String registerPath, String requestPath, Map<String, String> expectMap) throws Exception {
        //given
        RequestValues expect = new RequestValues(expectMap);
        HttpPathMatcher httpPathMatcher = new HttpPathMatcher(RequestMethod.GET, registerPath, TestClass.class.getDeclaredMethod("method"));

        //when
        Optional<HttpPathMatcher.MatchedMethod> optionalResolvedMethod = httpPathMatcher.matchMethod(RequestMethod.GET, requestPath);

        //then
        Assertions.assertThat(optionalResolvedMethod).isPresent();
        RequestValues actual = optionalResolvedMethod.get().getPathVariable();
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    public static Stream<Arguments> provideResult() {
        return Stream.of(
            Arguments.of("/{pv1}/{pv2}/{pv3}", "/path1/path2/path3", Map.of("pv1", "path1", "pv2", "path2", "pv3", "path3")),
            Arguments.of("/{pv1}/path2/path3", "/path1/path2/path3", Map.of("pv1", "path1")),
            Arguments.of("/{pv1}/{pv2}/path3", "/path1/path2/path3", Map.of("pv1", "path1", "pv2", "path2")),
            Arguments.of("/{pv1}/path2/{pv3}", "/path1/path2/path3", Map.of("pv1", "path1", "pv3", "path3")),
            Arguments.of("/path1/path2/{pv3}", "/path1/path2/path3", Map.of("pv3", "path3")),
            Arguments.of("/path1/{pv2}/{pv3}", "/path1/path2/path3", Map.of("pv2", "path2", "pv3", "path3")),
            Arguments.of("/path1/path2/{pv3}", "/path1/path2/path3", Map.of("pv3", "path3")),
            Arguments.of("/path1/path2/{pv3}", "/path1/path2/path3", Map.of("pv3", "path3")),
            Arguments.of("/**/{pv1}", "/path1", Map.of("pv1", "path1")),
            Arguments.of("/**/{pv1}", "/path1/path2", Map.of("pv1", "path2")),
            Arguments.of("/**/{pv1}", "/path1/path2/path3", Map.of("pv1", "path3")),
            Arguments.of("/**/{pv1}", "/path1/path2/path3/path4", Map.of("pv1", "path4")),
            Arguments.of("/**/{pv1}", "/path1/path2/path3/path4", Map.of("pv1", "path4")),
            Arguments.of("/**/{pv1}/path2", "/path1/path2", Map.of("pv1", "path1")),
            Arguments.of("/**/{pv1}/path3", "/path1/path2/path3", Map.of("pv1", "path2")),
            Arguments.of("/**/{pv1}/path4", "/path1/path2/path3/path4", Map.of("pv1", "path3")),
            Arguments.of("/**/path0/{pv1}", "/path0/path1", Map.of("pv1", "path1")),
            Arguments.of("/**/path1/{pv1}", "/path0/path1/path2", Map.of("pv1", "path2")),
            Arguments.of("/path1/**/path2/{pv1}", "/path1/path2/path3", Map.of("pv1", "path3")),
            Arguments.of("/path1/**/path3/{pv1}", "/path1/path2/path3/path4", Map.of("pv1", "path4")),
            Arguments.of("/path1/**/path4/{pv1}", "/path1/path2/path3/path4/path5", Map.of("pv1", "path5")),
            Arguments.of("/path1/**/path2/**/{pv1}/path6", "/path1/path2/path3/path4/path5/path6", Map.of("pv1", "path5")),
            Arguments.of("/path1/**/path2/**/{pv1}/path7", "/path1/path2/path3/path4/path5/path6/path7", Map.of("pv1", "path6")),
            Arguments.of("/path1/**/path3/{pv1}/**/{pv2}/path6", "/path1/path2/path3/path4/path5/path6", Map.of("pv1", "path4", "pv2", "path5")),
            Arguments.of("/path1/**/path3/**/{pv1}/**/{pv2}/path8", "/path1/path2/path3/path4/path5/path6/path7/path8", Map.of("pv1", "path4", "pv2", "path7")),
            Arguments.of("/path1/**/path3/**/{pv1}/**/{pv2}/path9", "/path1/path2/path3/path4/path5/path6/path7/path8/path9", Map.of("pv1", "path4", "pv2", "path8")),
            Arguments.of("/path1/**/path3/**/{pv1}/**/{pv2}/path9", "/path1/path2/path3/path4/path5/path6/path7/path8/path9", Map.of("pv1", "path4", "pv2", "path8")),
            Arguments.of("/path1/{pv1}/**/path3/**/{pv2}/**/{pv3}/path6", "/path1/path2/path3/path4/path5/path6", Map.of("pv1", "path2", "pv2", "path4", "pv3", "path5")),
            Arguments.of("/path1/{pv1}/**/path3/**/{pv2}/**/{pv3}/path7", "/path1/path2/path3/path4/path5/path6/path7", Map.of("pv1", "path2", "pv2", "path4", "pv3", "path6")),
            Arguments.of("/path1/{pv1}/**/{pv2}/**/{pv3}/**/path9", "/path1/path2/path3/path4/path5/path6/path9", Map.of("pv1", "path2", "pv2", "path3", "pv3", "path4")),
            Arguments.of("/path1/**/{pv1}/**/{pv2}/**/{pv3}/**/path9", "/path1/path2/path3/path4/path5/path6/path9", Map.of("pv1", "path2", "pv2", "path3", "pv3", "path4")),
            Arguments.of("/{pv1}/**", "/path1/path2", Map.of("pv1", "path1")),
            Arguments.of("/{pv1}/**", "/path1/path2/path3", Map.of("pv1", "path1")),
            Arguments.of("/{pv1}/**/path3", "/path1/path2/path3", Map.of("pv1", "path1")),
            Arguments.of("/{pv1}/**/path4", "/path1/path2/path3/path4", Map.of("pv1", "path1")),
            Arguments.of("/path1/{pv1}/**", "/path1/path2/path3", Map.of("pv1", "path2")),
            Arguments.of("/path1/{pv1}/**", "/path1/path2/path3/path4", Map.of("pv1", "path2")),
            Arguments.of("/path1/{pv1}/**", "/path1/path2/path3/path4", Map.of("pv1", "path2")),
            Arguments.of("/p1/{pv1}/**/p2/{pv2}/**/p3/{pv3}/**/p4/{pv4}/**/p5/{pv5}/**/p6",
                         "/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6",
                         Map.of("pv1", "r1",
                                "pv2", "r2",
                                "pv3", "r3",
                                "pv4", "r4",
                                "pv5", "r5"
                         )
            ),
            Arguments.of("/test/{path}/**/{path2}/test2", "/test/value1/1/2/3/4/value2/test2", Map.of("path", "value1", "path2", "value2"))
        );
    }

    public static class TestClass {
        public void method() {

        }
    }
}