package main.matcher;

import main.matcher.segment.PathMatchTestSuite;
import matcher.PathUrlMatcher;
import matcher.segment.PathUrl2;
import matcher.segment.PathVariableValue;
import matcher.segment.factory.SegmentChunkFactory;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PathUrl2MatcherTest {

    @DisplayName("http path 와 일치하면 resolve 데이터를 가져옵니다.")
    @PathMatchTestSuite.PathMatchTest
    void test1(String baseUrl, String requestPath, boolean expect) throws Exception {
        //given
        SegmentChunkFactory segmentChunkFactory = new SegmentChunkFactory(PathUrl2.from(baseUrl));
        PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(segmentChunkFactory);

        //when
        boolean actual = pathUrlMatcher.match(PathUrl2.from(requestPath)).isPresent();

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("pathVariable 로 부터 값을 가져옵니다.")
    @ParameterizedTest
    @MethodSource("providePathVariable")
    void test1(String baseUrl, String requestPath, Map<String, String> expectMap) throws Exception {
        //given
        PathVariableValue expect = new PathVariableValue(expectMap);
        SegmentChunkFactory segmentChunkFactory = new SegmentChunkFactory(PathUrl2.from(baseUrl));
        PathUrlMatcher pathUrlMatcher = PathUrlMatcher.from(segmentChunkFactory);

        //when
        Optional<PathVariableValue> optionalResolvedMethod = pathUrlMatcher.match(PathUrl2.from(requestPath));

        //then
        Assertions.assertThat(optionalResolvedMethod).isPresent();
        PathVariableValue actual = optionalResolvedMethod.get();
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    public static Stream<Arguments> providePathVariable() {
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
            Arguments.of("/path1/**/path3/**/{pv1}/**/{pv2}/path8",
                         "/path1/path2/path3/path4/path5/path6/path7/path8",
                         Map.of("pv1", "path4", "pv2", "path7")),
            Arguments.of("/path1/**/path3/**/{pv1}/**/{pv2}/path9",
                         "/path1/path2/path3/path4/path5/path6/path7/path8/path9", Map.of("pv1", "path4", "pv2", "path8")),
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
