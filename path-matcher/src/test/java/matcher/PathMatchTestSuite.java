package matcher;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class PathMatchTestSuite {

    @Target(METHOD)
    @Retention(RUNTIME)
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
    public @interface PathMatchGetValueTest {

    }

    @Target(METHOD)
    @Retention(RUNTIME)
    @ParameterizedTest
    @MethodSource("matcher.PathMatchTestSuite#provideMatchedPathVariableTestSuite")
    public @interface PathMatcherMatchedPathVariableTest {

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
