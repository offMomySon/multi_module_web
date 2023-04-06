package mapper;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import marker.RequestMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import vo.RequestParameters;

class HttpPathMatcherTest {

    @DisplayName("http path 와 일치하면 resolve 데이터를 가져옵니다.")
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
    void test1(String registerPath, String requestPath, boolean expect) throws Exception {
        //given
        HttpPathMatcher httpPathMatcher = new HttpPathMatcher(RequestMethod.GET, registerPath, TestClass.class.getDeclaredMethod("method"));

        //when
        boolean actual = httpPathMatcher.matchMethod(RequestMethod.GET, requestPath).isPresent();

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("pathVariable 로 부터 값을 가져옵니다.")
    @ParameterizedTest
    @MethodSource("provideResult")
    void test1(String registerPath, String requestPath, Map<String, String> expectMap) throws Exception {
        //given
        RequestParameters expect = new RequestParameters(expectMap);
        HttpPathMatcher httpPathMatcher = new HttpPathMatcher(RequestMethod.GET, registerPath, TestClass.class.getDeclaredMethod("method"));

        //when
        Optional<HttpPathMatcher.MatchedMethod> optionalResolvedMethod = httpPathMatcher.matchMethod(RequestMethod.GET, requestPath);

        //then
        Assertions.assertThat(optionalResolvedMethod).isPresent();
        RequestParameters actual = optionalResolvedMethod.get().getPathVariable();
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