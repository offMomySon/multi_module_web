package pretask.pattern;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class WildCardPathUrlTest {
    private static final String WILD_CARD = "/*";

    @DisplayName("basePath 가 wild card 를 가지고 있지 않으면 exception 이 발생합니다.")
    @Test
    void test() throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> new WildCardPathMatcher("/test"));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("basePathUrl 가 empty 이면, 언제나 true 를 반환합니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "/test.txt",
        "/test.img",
        "/p1/test.txt",
        "/p1/p2/test.txt",
        "/p1/p2/p3/test.txt",
        "/p1/p2/p3/test.txt",
        "test.fail",
        "/p1/test.fail",
        "/p1/p2/test.fail",
        "/p1/p2/p3/test.fail",
    })
    void test(String requestPath) throws Exception {
        //given
        WildCardPathMatcher wildCardPathUrl = new WildCardPathMatcher(WILD_CARD);

        //when
        boolean actual = wildCardPathUrl.isMatch(requestPath);

        //then
        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("매칭 여부를 판별합니다.")
    @ParameterizedTest
    @CsvSource(value = {
        "/p1/*, /p1, true",
        "/p1/*, /p1/p2, true",
        "/p1/*, /p1/p2/p3, true",
        "/p1/p2/*, /p1, false",
        "/p1/p2/*, /p1/p2, true",
        "/p1/p2/*, /p1/p2/p3, true",
        "/p1/*, /p1DoesNotEnd, false",
        "/p1/p2/*, /p1/p2DoesNotEnd, false",
        "/p1/p2/*, /p1/diffPath, false",
        "/p1/p2/*, /diffPath/p2, false",
    })
    void test2(String basePath, String requestPath, boolean expect) throws Exception {
        //given
        WildCardPathMatcher wildCardPathUrl = new WildCardPathMatcher(basePath);

        //when
        boolean actual = wildCardPathUrl.isMatch(requestPath);

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

}