package filter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PatternUrlTest {

    @DisplayName("match 여부를 확인합니다.")
    @ParameterizedTest
    @CsvSource(value = {
        "/*,/p1,true",
        "/*,/p1/p2/p3/p4,true",
        "/*,/1/2/3/4/5/6/,true",
        "/1/*,/1/2/3,true",
        "/1/*,/1/2/3/4/5,true",
        "/1/2/*,/1/2/3/4/5,true",
        "/1/fail/*,/1/2/3/4/5,false",
        "/,/,true",
        "/,/p1,false",
        "/,/p1/p2/p3/p4,false",
        "*.text,/test/fff.text,true",
        "*.text,/test/fff.notText,false",
    })
    void test(PatternUrl patternUrl, String requestUrl, boolean expect) throws Exception {
        //given
        //when
        boolean actual = patternUrl.isMatch(requestUrl);

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }
}