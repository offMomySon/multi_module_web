//package request;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//
//class RequestLineTest {
//    @ParameterizedTest
//    @DisplayName("method, path, version 으로 세부분으로 나누어지지 않으면 exception 이 발생합니다.")
//    @ValueSource(strings = {
//        "GET",
//        "GET /test",
//        "GET /test Http/version.1 anotherElement",
//        "GET /test http/1.1 anotherElement anotherElement2",
//        "POST",
//        "POST /post",
//        "POST /post http/1.1 anotherElement",
//        "POST /test http/1.1 anotherElement anotherElement2",
//    })
//    void test1(String request) {
//        //given
//        //when
//        Throwable actual = Assertions.catchThrowable(() -> RequestLine.of(request));
//
//        //then
//        Assertions.assertThat(actual).isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @ParameterizedTest
//    @DisplayName("path 의 query string 이 2개 이상 나오면 exception 이 발생합니다.")
//    @ValueSource(strings = {
//        "GET /test?query=1?query=2 http/1.1",
//        "GET /test?query=1?query=2?query=3 http/1.1",
//        "GET /test?query=1?query=2?query=3?query=4 http/1.1",
//        "POST /test?query=1?query=2 http/1.1",
//        "POST /test?query=1?query=2?query=3 http/1.1",
//        "POST /test?query=1?query=2?query=3?query=4 http/1.1",
//    })
//    void test2(String request) {
//        //given
//        //when
//        Throwable actual = Assertions.catchThrowable(() -> RequestLine.of(request));
//
//        //then
//        Assertions.assertThat(actual).isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @ParameterizedTest
//    @DisplayName("올바른 request line 포멧 이면 request line 객체를 생성합니다.")
//    @ValueSource(strings = {
//        "GET /test HTTP/1.1",
//        "GET /test/beta HTTP/1.1",
//        "GET /test/beta?key=value HTTP/1.1",
//        "POST /post HTTP/1.1",
//    })
//    void test3(String request) {
//        //given
//        //when
//        Throwable actual = Assertions.catchThrowable(() -> RequestLine.of(request));
//
//        //then
//        Assertions.assertThat(actual)
//            .isNull();
//    }
//
//
//}