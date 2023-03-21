package mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import vo.HttpMethod;

class HttpPathResolverTest {

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

        "/path1/**/path1/{pv1}/**/{pv1}/path1, /path1/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/**/{pv1}/path1, /path1/path1/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/**/{pv1}/path1, /path1/path1/path1/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/**/{pv1}/path1, /path1/path2/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path1/path1/path1, true",
        "/path1/**/path1/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1, false",
        "/path1/**/path1/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path1, false",
        "/path1/**/path1/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path1/path1, false",

        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path1/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path2/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path2/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path2/path2/path1/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path2/path2/path1/path2/path1/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path2/path2/path1/path2/path1/path2/path1, true",
        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path1/path1/path1/path2, false",
        "/path1/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path1/path1, false",

        "/path1/{pv1}/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path1/path1/path1/path1, false",
        "/path1/{pv1}/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path1/path1/path1/path1/path1, true",
        "/path1/{pv1}/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path1/path1/path1/path1/path1, true",
        "/path1/{pv1}/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path2/path1/path1/path1/path1, true",
        "/path1/{pv1}/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path1/path1/path1, true",
        "/path1/{pv1}/**/path1/**/{pv1}/**/{pv1}/path1, /path1/path2/path2/path1/path1/path1, false",


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
        HttpPathResolver httpPathResolver = new HttpPathResolver(HttpMethod.GET, registerPath, TestClass.class.getDeclaredMethod("method"));

        //when
        boolean actual = httpPathResolver.resolve(HttpMethod.GET, requestPath).isPresent();

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    public static class TestClass {
        public void method() {

        }
    }
}