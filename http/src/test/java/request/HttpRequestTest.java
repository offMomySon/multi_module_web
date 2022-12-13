package request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static java.nio.charset.StandardCharsets.UTF_8;

class HttpRequestTest {
    @DisplayName("http request message 포멧에 맞게 데이터를 파싱합니다.")
    @ParameterizedTest
    @MethodSource("provideHttpRequest")
    void test3(String request) throws IOException {
        // given
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getBytes(UTF_8));

        //when
        Throwable actual = Assertions.catchThrowable(() -> HttpRequest.parse(byteArrayInputStream));

        //then
        Assertions.assertThat(actual).isNull();
    }

    private static Stream<Arguments> provideHttpRequest() {
        return Stream.of(
            Arguments.of("GET /doc/test.html HTTP/1.1\r\n" +
                             "Host: www.test1.1.com\r\n" +
                             "Accept : text/html, application/xhtml.xml\r\n" +
                             "Accept-Language : en-us\r\n" +
                             "\r\n" +
                             "test body")
        );
    }
}