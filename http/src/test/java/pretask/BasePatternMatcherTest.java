//package pretask;
//
//import pretask.pattern.BasePatternMatcher;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//class BasePatternMatcherTest {
//
//    @DisplayName("match 를 확인한다.")
//    @ParameterizedTest
//    @CsvSource(value = {
//        "/p1, /p1, true",
//        "/p1, /p1/p2, false",
//        "/p1, /p1/p2/p3, false",
//        "/p1/p2, /p1/p2, true",
//        "/p1/p2, /p1/p2/p3, false",
//        "/p1/p2/p3, /p1/p2/p3, true",
//    })
//    void test(BasePatternMatcher basePatternUrl, String requestUrl, boolean expect) throws Exception {
//        //given
//        //when
//        boolean actual = basePatternUrl.isMatch(requestUrl);
//
//        //then
//        Assertions.assertThat(actual).isEqualTo(expect);
//    }
//}