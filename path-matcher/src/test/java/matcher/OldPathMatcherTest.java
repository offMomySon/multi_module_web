package matcher;

import java.util.Map;
import java.util.stream.Stream;
import matcher.path.PathUrl;
import matcher.path.PathVariable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static matcher.PathMatchTestSuite.PathMatchGetValueTest;

class OldPathMatcherTest {
    @DisplayName("matcher 에 대해 path 가 매칭되면, 존재하는 값을 반환합니다.")
    @PathMatchGetValueTest
    void Given_BasePahAndRequestPath_When_Matched_Then_GetPresentValue(String basePath, String requestPath, boolean expect) throws Exception {
        // given
        PathUrl basePathUrl = PathUrl.of(basePath);
        PathUrl requestPathUrl = PathUrl.of(requestPath);

        OldPathMatcher oldPathMatcher = OldPathMatcher.of(basePathUrl);

        // when
        boolean actual = oldPathMatcher.match(requestPathUrl).isPresent();

        // then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("matcher 에 대해 path 가 매칭되면, pathVariable 값을 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideMatchedPathVariableTestSuite")
    void Given_BasePathAndRequestPath_When_Matched_Then_GetPathVariable(String basePath, String requestPath, Map<String, String> expectMap) throws Exception {
        // given
        PathUrl basePathUrl = PathUrl.of(basePath);
        PathUrl requestPathUrl = PathUrl.of(requestPath);
        PathVariable expect = new PathVariable(expectMap);

        OldPathMatcher oldPathMatcher = OldPathMatcher.of(basePathUrl);

        // when
        PathVariable actual = oldPathMatcher.match(requestPathUrl)
            .orElseThrow(() -> new RuntimeException("Does not exist pathVariable."));

        // then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    public static Stream<Arguments> provideMatchedPathVariableTestSuite() {
        return Stream.of(
            Arguments.of("/GET/{pv1}/{pv2}/{pv3}", "/GET/path1/path2/path3", Map.of("pv1", "path1", "pv2", "path2", "pv3", "path3")),
            Arguments.of("/GET/{pv1}/path2/path3", "/GET/path1/path2/path3", Map.of("pv1", "path1")),
            Arguments.of("/GET/{pv1}/{pv2}/path3", "/GET/path1/path2/path3", Map.of("pv1", "path1", "pv2", "path2")),
            Arguments.of("/GET/{pv1}/path2/{pv3}", "/GET/path1/path2/path3", Map.of("pv1", "path1", "pv3", "path3")),
            Arguments.of("/GET/path1/path2/{pv3}", "/GET/path1/path2/path3", Map.of("pv3", "path3")),
            Arguments.of("/GET/path1/{pv2}/{pv3}", "/GET/path1/path2/path3", Map.of("pv2", "path2", "pv3", "path3")),
            Arguments.of("/GET/path1/path2/{pv3}", "/GET/path1/path2/path3", Map.of("pv3", "path3")),
            Arguments.of("/GET/path1/path2/{pv3}", "/GET/path1/path2/path3", Map.of("pv3", "path3")),
            Arguments.of("/GET/**/{pv1}", "/GET/path1", Map.of("pv1", "path1")),
            Arguments.of("/GET/**/{pv1}", "/GET/path1/path2", Map.of("pv1", "path2")),
            Arguments.of("/GET/**/{pv1}", "/GET/path1/path2/path3", Map.of("pv1", "path3")),
            Arguments.of("/GET/**/{pv1}", "/GET/path1/path2/path3/path4", Map.of("pv1", "path4")),
            Arguments.of("/GET/**/{pv1}", "/GET/path1/path2/path3/path4", Map.of("pv1", "path4")),
            Arguments.of("/GET/**/{pv1}/path2", "/GET/path1/path2", Map.of("pv1", "path1")),
            Arguments.of("/GET/**/{pv1}/path3", "/GET/path1/path2/path3", Map.of("pv1", "path2")),
            Arguments.of("/GET/**/{pv1}/path4", "/GET/path1/path2/path3/path4", Map.of("pv1", "path3")),
            Arguments.of("/GET/**/path0/{pv1}", "/GET/path0/path1", Map.of("pv1", "path1")),
            Arguments.of("/GET/**/path1/{pv1}", "/GET/path0/path1/path2", Map.of("pv1", "path2")),
            Arguments.of("/GET/path1/**/path2/{pv1}", "/GET/path1/path2/path3", Map.of("pv1", "path3")),
            Arguments.of("/GET/path1/**/path3/{pv1}", "/GET/path1/path2/path3/path4", Map.of("pv1", "path4")),
            Arguments.of("/GET/path1/**/path4/{pv1}", "/GET/path1/path2/path3/path4/path5", Map.of("pv1", "path5")),
            Arguments.of("/GET/path1/**/path2/**/{pv1}/path6", "/GET/path1/path2/path3/path4/path5/path6", Map.of("pv1", "path5")),
            Arguments.of("/GET/path1/**/path2/**/{pv1}/path7", "/GET/path1/path2/path3/path4/path5/path6/path7", Map.of("pv1", "path6")),
            Arguments.of("/GET/path1/**/path3/{pv1}/**/{pv2}/path6", "/GET/path1/path2/path3/path4/path5/path6", Map.of("pv1", "path4", "pv2", "path5")),
            Arguments.of("/GET/path1/**/path3/**/{pv1}/**/{pv2}/path8",
                         "/GET/path1/path2/path3/path4/path5/path6/path7/path8",
                         Map.of("pv1", "path4", "pv2", "path7")),
            Arguments.of("/POST/path1/**/path3/**/{pv1}/**/{pv2}/path9",
                         "/POST/path1/path2/path3/path4/path5/path6/path7/path8/path9", Map.of("pv1", "path4", "pv2", "path8")),
            Arguments.of("/POST/path1/**/path3/**/{pv1}/**/{pv2}/path9", "/POST/path1/path2/path3/path4/path5/path6/path7/path8/path9", Map.of("pv1", "path4", "pv2", "path8")),
            Arguments.of("/POST/path1/{pv1}/**/path3/**/{pv2}/**/{pv3}/path6", "/POST/path1/path2/path3/path4/path5/path6", Map.of("pv1", "path2", "pv2", "path4", "pv3", "path5")),
            Arguments.of("/POST/path1/{pv1}/**/path3/**/{pv2}/**/{pv3}/path7", "/POST/path1/path2/path3/path4/path5/path6/path7", Map.of("pv1", "path2", "pv2", "path4", "pv3", "path6")),
            Arguments.of("/POST/path1/{pv1}/**/{pv2}/**/{pv3}/**/path9", "/POST/path1/path2/path3/path4/path5/path6/path9", Map.of("pv1", "path2", "pv2", "path3", "pv3", "path4")),
            Arguments.of("/POST/path1/**/{pv1}/**/{pv2}/**/{pv3}/**/path9", "/POST/path1/path2/path3/path4/path5/path6/path9", Map.of("pv1", "path2", "pv2", "path3", "pv3", "path4")),
            Arguments.of("/POST/{pv1}/**", "/POST/path1/path2", Map.of("pv1", "path1")),
            Arguments.of("/POST/{pv1}/**", "/POST/path1/path2/path3", Map.of("pv1", "path1")),
            Arguments.of("/POST/{pv1}/**/path3", "/POST/path1/path2/path3", Map.of("pv1", "path1")),
            Arguments.of("/POST/{pv1}/**/path4", "/POST/path1/path2/path3/path4", Map.of("pv1", "path1")),
            Arguments.of("/POST/path1/{pv1}/**", "/POST/path1/path2/path3", Map.of("pv1", "path2")),
            Arguments.of("/POST/path1/{pv1}/**", "/POST/path1/path2/path3/path4", Map.of("pv1", "path2")),
            Arguments.of("/POST/path1/{pv1}/**", "/POST/path1/path2/path3/path4", Map.of("pv1", "path2")),
            Arguments.of("/POST/p1/{pv1}/**/p2/{pv2}/**/p3/{pv3}/**/p4/{pv4}/**/p5/{pv5}/**/p6",
                         "/POST/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6",
                         Map.of("pv1", "r1",
                                "pv2", "r2",
                                "pv3", "r3",
                                "pv4", "r4",
                                "pv5", "r5"
                         )
            ),
            Arguments.of("/POST/test/{path}/**/{path2}/test2", "/POST/test/value1/1/2/3/4/value2/test2", Map.of("path", "value1", "path2", "value2"))
        );
    }
}