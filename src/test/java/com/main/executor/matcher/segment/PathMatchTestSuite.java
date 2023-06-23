package com.main.executor.matcher.segment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class PathMatchTestSuite {
    
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
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
    public @interface PathMatchTest {
    }

}
