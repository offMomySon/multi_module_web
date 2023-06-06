package filter.pattern;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class WildCardFileExtensionUrlTest {

    @DisplayName("wild card file name 이 시작인 pattern 을 받지 않으면 exception 이 발생합니다.")
    @Test
    void test() throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> new WildCardFileExtensionUrl("aaa.text"));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("file 확장자가 일치하는지 확인합니다.")
    @ParameterizedTest
    @CsvSource(value = {
        "*.txt, /test.txt, true",
        "*.img, /test.img, true",
        "*.txt, /p1/test.txt, true",
        "*.txt, /p1/p2/test.txt, true",
        "*.txt, /p1/p2/p3/test.txt, true",
        "*.txt, /p1/p2/p3/test.txt, true",
        "*.txt, test.fail, false",
        "*.txt, /p1/test.fail, false",
        "*.txt, /p1/p2/test.fail, false",
        "*.txt, /p1/p2/p3/test.fail, false",
    })
    void test(String baseFileExtension, String requestPath, boolean expect) throws Exception {
        //given
        WildCardFileExtensionUrl wildCardFileExtensionUrl = new WildCardFileExtensionUrl(baseFileExtension);

        //when
        boolean actual = wildCardFileExtensionUrl.isMatch(requestPath);

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("requestPath 에 delimiter 가 존재하지 않으면 실패합니다.")
    @ParameterizedTest
    @CsvSource(value = {
        "*.txt, test.txt, false",
        "*.txt, test.img, false",
        "*.txt, base.org, false",
    })
    void test2(String baseFileExtension, String requestPath, boolean expect) throws Exception {
        //given
        WildCardFileExtensionUrl wildCardFileExtensionUrl = new WildCardFileExtensionUrl(baseFileExtension);

        //when
        boolean actual = wildCardFileExtensionUrl.isMatch(requestPath);

        //then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("File delimiter 보다 path delimiter 가 뒤에 있으면 실패합니다.")
    @ParameterizedTest
    @CsvSource(value = {
        "*.txt, test.t/xt, false",
        "*.txt, test.im/g, false",
        "*.txt, base.org/, false",
    })
    void test3(String baseFileExtension, String requestPath, boolean expect) throws Exception {
        //given
        WildCardFileExtensionUrl wildCardFileExtensionUrl = new WildCardFileExtensionUrl(baseFileExtension);

        //when
        boolean actual = wildCardFileExtensionUrl.isMatch(requestPath);

        //then
        Assertions.assertThat(actual).isFalse();
    }

}