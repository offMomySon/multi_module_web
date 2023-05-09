package mapper.segmentv3;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class PathUrlMatcherTest {
    @DisplayName("methodPath 와 requestUrl 가 매칭되면 pathvariable 을 가져옵니다.")
    @ParameterizedTest
    @CsvSource({
        "/path1, /path1, true",
        "/path1, /path2, false",
        "/path1/path2, /path1/path2, true",
        "/path1/path2, /path1/path3, false",
        "/path1/path2/path3, /path1/path2/path3, true",
        "/path1/path2/path3, /path1/path2/path4, false",
        "/path1/**, /path1/path2, true",
        "/path2/**, /path1/path2, false",
        "/path1/**, /path1/path2/path3, true",
        "/path2/**, /path1/path2/path3, false",
        "/path1/**/path2, /path1/path2/path2, true",
        "/path1/**/path2, /path1/path2, true",
        "/path1/**/path2, /path1/path2/path3, false",
        "/path1/**/path2/path3, /path1/path2/path3, true",
        "/path1/**/path2/path3/path4, /path1/path2/path3/path4, true",
        "/path1/**/path2/path3, /path1/path2/path4, false",
        "/path1/**/path2/**/path3, /path1/w1/path2/w1/path3, true",
        "/path1/**/path2/**/path3, /path1/w1/w2/path2/w1/w2/path3, true",
        "/path1/**/path2/**/path3, /path1/path2/path2/path2/path3/path3/path3, true",
        "/path1/**/path2/**/path3, /path1/path2/path2/path2/path3/path4/path3, true",
        "/path1/**/path2/**/path3, /path1/path2/path2/path2/path3/path3/path4, false",
        "/path1/**/path2/path3/**/path4, /path1/path2/path2/path2/path3/path3/path3/path4/path4, true",
        "/path1/**/path2/path3/**/path4, /path1/path2/path2/path2/path3/path3/path2/path4/path4, true",
        "/path1/**/path2/path3/**/path4, /path1/path2/path2/path2/path3/path3/path2/path3/path4/path4, true",
        "/path1/**/path2/path3/**/path4, /path1/path2/path2/path2/path3/p1/p2/path2/path4, true",
        "/p1/**/p2/**/p3/**/p4/**/p5/**/p6, " +
            "/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6," +
            "true",

        "/path1, /path1, true",
        "/path1, /path2, false",
        "/{pv1}, /path1, true",
        "/path1/path2, /path1/path2, true",
        "/path1/path2, /path1/path3, false",
        "/{pv1}/path2, /path1/path2, true",
        "/path1/{pv1}, /path1/path3, true",
        "/{pv1}/{pv2}, /path1/path2, true",
        "/path1/path2/path3, /path1/path2/path3, true",
        "/path1/path2/path3, /path1/path2/path4, false",
        "/{pv1}/{pv2}/{pv3}, /path1/path2/path3, true",
        "/{pv1}/path1/path2, /path1/path1/path2, true",
        "/{pv1}/{pv2}/path2, /path1/path1/path2, true",
        "/{pv1}/path1/{pv2}, /path1/path1/path2, true",
        "/path1/{pv1}/{pv2}, /path1/path1/path2, true",
        "/path1/{pv1}/path2, /path1/path1/path2, true",
        "/path1/{pv1}/{pv2}, /path1/path2/path3, true",
        "/path1/path2/{pv1}, /path1/path2/path4, true",

        "/**, /, true",
        "/**, /path1, true",
        "/**, /path1, true",
        "/**, /path1/path2, true",
        "/**, /path1/path2/path3, true",
        "/**, /path1/path2/path3/path4, true",
        "/**, /path1/path2/path3/path4/path5, true",

        "/path1/**, /, false",
        "/path1/**, /path1, true",
        "/path1/**, /path1/path2, true",
        "/path1/**, /path1/path2/path3, true",
        "/path1/**, /path1/path2/path3/path4, true",
        "/path1/**, /path2, false",
        "/path1/**, /path2/path2, false",
        "/path1/**, /path2/path2/path3, false",
        "/path1/**, /path2/path2/path3/path4, false",

        "/**/{pv1}, /, false",
        "/**/{pv1}, /path1, true",
        "/**/{pv1}, /path1/path2, true",
        "/**/{pv1}, /path1/path2/path3, true",
        "/**/{pv1}, /path1/path2/path3/path4, true",

        "/**/{pv1}/path1, /, false",
        "/**/{pv1}/path1, /path1, false",
        "/**/{pv1}/path1, /path1/path1, true",
        "/**/{pv1}/path1, /path1/path1/path1, true",
        "/**/{pv1}/path1, /path1/path1/path1/path1, true",
        "/**/{pv1}/path1, /path1/path1/path1/path1/path1, true",

        "/**/path1/{pv1}, /, false",
        "/**/path1/{pv1}, /path1, false",
        "/**/path1/{pv1}, /path1/path1, true",
        "/**/path1/{pv1}, /path1/path1/path1, true",
        "/**/path1/{pv1}, /path1/path2, true",
        "/**/path1/{pv1}, /path1/path1/path3, true",
        "/**/path1/{pv1}, /path1/path1/path1/path3, true",
        "/**/path1/{pv1}, /path1/path2/path3, false",
        "/**/path1/{pv1}, /path1/path2/path3/path4, false",

        "/path1/**/{pv1}, /, false",
        "/path1/**/{pv1}, /path1, false",
        "/path1/**/{pv1}, /path1/path1, true",
        "/path1/**/{pv1}, /path1/path2, true",
        "/path1/**/{pv1}, /path1/path1/path1, true",
        "/path1/**/{pv1}, /path2/path1, false",
        "/path1/**/{pv1}, /path2/path1/path1, false",

        "/path1/**/{pv1}/path1, /, false",
        "/path1/**/{pv1}/path1, /path1, false",
        "/path1/**/{pv1}/path1, /path1/path1, false",
        "/path1/**/{pv1}/path1, /path1/path1/path1, true",
        "/path1/**/{pv1}/path1, /path1/path1/path1/path1, true",
        "/path1/**/{pv1}/path1, /path1/path1/path1/path1/path1, true",
        "/path1/**/{pv1}/path1, /path2/path1/path1/path1/path1, false",
        "/path1/**/{pv1}/path1, /path1/path1/path1/path1/path2, false",

        "/path1/**/path1/{pv1}, /, false",
        "/path1/**/path1/{pv1}, /path1, false",
        "/path1/**/path1/{pv1}, /path1/path1, false",
        "/path1/**/path1/{pv1}, /path1/path1/path1, true",
        "/path1/**/path1/{pv1}, /path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}, /path1/path1/path2/path1, false",
        "/path1/**/path1/{pv1}, /path1/path1/path1, true",
        "/path1/**/path1/{pv1}, /path1/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}, /path2/path1/path1, false",
        "/path1/**/path1/{pv1}, /path1/path2/path1, false",

        "/path1/**/path1/{pv1}/path1, /, false",
        "/path1/**/path1/{pv1}/path1, /path1, false",
        "/path1/**/path1/{pv1}/path1, /path1/path1, false",
        "/path1/**/path1/{pv1}/path1, /path1/path1/path1, false",
        "/path1/**/path1/{pv1}/path1, /path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/path1, /path1/path1/path2/path1, true",
        "/path1/**/path1/{pv1}/path1, /path1/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/path1, /path1/path1/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/path1, /path1/path2/path1/path1, false",
        "/path1/**/path1/{pv1}/path1, /path1/path1/path1/path2, false",
        "/path1/**/path1/{pv1}/path1, /path2/path1/path1/path1, false",
        "/path1/**/path1/{pv1}/path1, /path2/path1/path1/path1/path1, false",

        "/path1/**/path1/**/{pv1}/path1, /path1/path1/path1, false",
        "/path1/**/path1/**/{pv1}/path1, /path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/path1, /path1/path1/path1/path1/path1, true",
        "/path1/**/path2/**/{pv1}/path1, /path1/path2/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/path1, /path1/path2/path2/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/path1, /path1/path2/path2/path1/path2/path1/path1, true",
        "/path1/**/path1/**/{pv1}/path1, /path1/path2/path2/path1/path2/path2/path1/path1, true",
        "/path1/**/path1/**/{pv1}/path1, /path1/path2/path2/path1/path2/path2/path2/path1, true",
        "/path1/**/path1/**/{pv1}/path1, /path1/path2/path2/path1/path2/path2/path2/path2, false",
        "/path1/**/path1/**/{pv1}/path1, /path1/path2/path2/path2/path2/path2/path2/path1, false",
        "/path1/**/path1/**/{pv1}/path1, /path1/path2/path2/path2/path2/path2/path2/path1, false",
        "/path1/**/path1/**/{pv1}/path1, /path2/path2/path2/path2/path2/path2/path2/path1, false",

        "/path1/**/path1/{pv1}/**/{pv2}/path1, /path1/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/**/{pv2}/path1, /path1/path1/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/**/{pv2}/path1, /path1/path1/path1/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/**/{pv2}/path1, /path1/path2/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/**/{pv2}/path1, /path1/path2/path2/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/**/{pv2}/path1, /path1/path2/path2/path1, false",
        "/path1/**/path1/{pv1}/**/{pv2}/path1, /path1/path2/path2/path1/path1, false",
        "/path1/**/path1/{pv1}/**/{pv2}/path1, /path1/path2/path2/path1/path1/path1, false",

        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path1/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path2/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path2/path2/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path2/path2/path1/path2/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path2/path2/path1/path2/path2/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path2/path2/path1/path2/path2/path1/path2/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path2/path2/path1/path2/path2/path1/path2/path1/path2/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path1/path1/path1/path2, false",
        "/path1/**/path1/**/{pv1}/**/{pv2}/path1, /path1/path2/path2/path1/path1/path1, false",

        "/path1/{pv1}/**/path1/**/{pv2}/**/{pv3}/path1, /path1/path1/path1/path1/path1, false",
        "/path1/{pv1}/**/path1/**/{pv2}/**/{pv3}/path1, /path1/path1/path1/path1/path1/path1, true",
        "/path1/{pv1}/**/path1/**/{pv2}/**/{pv3}/path1, /path1/path1/path1/path1/path1/path1, true",
        "/path1/{pv1}/**/path1/**/{pv2}/**/{pv3}/path1, /path1/path2/path1/path1/path1/path1, true",
        "/path1/{pv1}/**/path1/**/{pv2}/**/{pv3}/path1, /path1/path2/path2/path1/path1/path1/path1, true",
        "/path1/{pv1}/**/path1/**/{pv2}/**/{pv3}/path1, /path1/path2/path2/path1/path1/path1, false",


        "/{pv1}, /, false",
        "/{pv1}/**, /, false",
        "/{pv1}/**, /path1, true",
        "/{pv1}/**, /path1/path2, true",
        "/{pv1}/**, /path1/path2/path3, true",
        "/{pv1}/**, /path1/path2/path3/path4, true",

        "/{pv1}/**/path1, /, false",
        "/{pv1}/**/path1, /path1, false",
        "/{pv1}/**/path1, /path1/path1, true",
        "/{pv1}/**/path1, /path1/path1/path1, true",
        "/{pv1}/**/path1, /path1/path1/path1/path1, true",
        "/{pv1}/**/path1, /path2, false",
        "/{pv1}/**/path1, /path1/path2, false",

        "/{pv1}/path1/**, /, false",
        "/{pv1}/path1/**, /path1, false",
        "/{pv1}/path1/**, /path1/path1, true",
        "/{pv1}/path1/**, /path1/path1/path1, true",
        "/{pv1}/path1/**, /path1/path1/path1/path1, true",
        "/{pv1}/path1/**, /path1/path2, false",
        "/{pv1}/path1/**, /path1/path2/path1, false",
        "/{pv1}/path1/**, /path1/path2/path1/path1, false",

        "/path1/{pv1}/**, /, false",
        "/path1/{pv1}/**, /path1, false",
        "/path1/{pv1}/**, /path1/path1, true",
        "/path1/{pv1}/**, /path1/path1/path1, true",
        "/path1/{pv1}/**, /path1/path1/path1/path1, true",
        "/path1/{pv1}/**, /path2, false",
        "/path1/{pv1}/**, /path2/path2, false",
        "/path1/{pv1}/**, /path2/path2/path2, false",
        "/path1/{pv1}/**, /path2/path2/path2/path2, false",

        "/p1/{pv1}/**/p2/{pv2}/**/p3/{pv3}/**/p4/{pv4}/**/p5/{pv5}/**/p6, " +
            "/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6, " +
            "true",

        "/p1/**/p2/**/p3/**/p4/**/p5/**/p6, " +
            "/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6/r6/p1/r1/p2/r2/p3/r3/p4/r4/p5/r5/p6," +
            "true",
        "/, /, true",
        "/, /path1, false"
    })
    void test1(String baseUrl, String requestUrl, boolean expect) throws Exception {
        //given
        PathUrlMatcher pathUrlMatcher = new PathUrlMatcher(PathUrl.from(baseUrl));
        //whenD
        Optional<PathVariable> optionalActual = pathUrlMatcher.match(PathUrl.from(requestUrl));

        //then
        Assertions.assertThat(optionalActual.isPresent()).isEqualTo(expect);
    }

    @DisplayName("처음 일치한 pathVariable 을 찾아온다.")
    @ParameterizedTest
    @MethodSource("providePathVariable")
    void test(String _baseUrl, String _requestUrl, PathVariable expect) throws Exception {
        //given
        PathUrlMatcher pathUrlMatcher = new PathUrlMatcher(PathUrl.from(_baseUrl));
        //when
        Optional<PathVariable> optionalActual = pathUrlMatcher.match(PathUrl.from(_requestUrl));

        //then
        Assertions.assertThat(optionalActual).isPresent();
        PathVariable actual = optionalActual.get();
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    public static Stream<Arguments> providePathVariable() {
        return Stream.of(
            Arguments.of("/{pv1}/{pv2}/{pv3}", "/path1/path2/path3", new PathVariable(Map.of("pv1", "path1", "pv2", "path2", "pv3", "path3"))),
            Arguments.of("/{pv1}/path1/{pv2}", "/path1/path1/path2", new PathVariable(Map.of("pv1", "path1", "pv2", "path2"))),
            Arguments.of("/**/{pv1}", "/path1/path2/path3", new PathVariable(Map.of("pv1", "path3"))),
            Arguments.of("/**/{pv1}/**/{pv2}/**/{pv3}", "/path1/path2/path3/path4/path5/path6", new PathVariable(Map.of("pv1", "path1", "pv2", "path2", "pv3", "path6"))),
            Arguments.of("/**/{pv1}/path1", "/path1/path1/path1", new PathVariable(Map.of("pv1", "path1"))),
            Arguments.of("/path1/**/{pv1}", "/path1/path1/path2", new PathVariable(Map.of("pv1", "path2"))),
            Arguments.of("/path1/**/{pv1}/**/{pv2}", "/path1/path2/path3/path4/path5/path6", new PathVariable(Map.of("pv1", "path2", "pv2", "path6"))),
            Arguments.of("/path1/**/{pv1}/path1", "/path1/path2/path1/path2/path1", new PathVariable(Map.of("pv1", "path2"))),
            Arguments.of("/path1/**/path1/{pv1}", "/path1/path1/path3/path1/path1", new PathVariable(Map.of("pv1", "path1"))),
            Arguments.of("/path1/**/path1/{pv1}/**/{pv2}/path1", "/path1/path2/path2/path1/path3/path2/path1", new PathVariable(Map.of("pv1", "path3", "pv2", "path2"))),
            Arguments.of("/path1/**/path1/**/{pv1}/**/{pv2}/path1", "/path1/path2/path1/path3/path5/path1", new PathVariable(Map.of("pv1", "path3", "pv2", "path5"))),
            Arguments.of("/path1/{pv1}/**/path1/**/{pv2}/**/{pv3}/path1", "/path1/path2/path1/path3/path5/path1", new PathVariable(Map.of("pv1", "path2", "pv2", "path3", "pv3", "path5"))),
            Arguments.of("/{pv1}/**/path1", "/path4/path1", new PathVariable(Map.of("pv1", "path4")))
        );

    }
}