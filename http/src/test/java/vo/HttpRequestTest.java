//package vo;
//
//import java.io.BufferedInputStream;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Stream;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import static java.nio.charset.StandardCharsets.UTF_8;
//
//class HttpRequestTest {
//    @DisplayName("http request message 포멧에 맞게 데이터를 파싱합니다.")
//    @ParameterizedTest
//    @MethodSource("provideHttpRequest")
//    void test1(String request) {
//        // given
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getBytes(UTF_8));
//
//        //when
////        Throwable actual = Assertions.catchThrowable(() -> HttpRequest.parse(byteArrayInputStream));
//
//        //then
//        Assertions.assertThat(actual).isNull();
//    }
//
//    @DisplayName("http request message 의 header 파싱합니다.")
//    @ParameterizedTest
//    @MethodSource("provideHeader")
//    void test3(String request, Map<String, Set<String>> expect) {
//        // given
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getBytes(UTF_8));
//        HttpRequest httpRequest = HttpRequest.parse(byteArrayInputStream);
//
//        //when
//        Map<String, Set<String>> actual = httpRequest.getHeader();
//
//        //then
//        Assertions.assertThat(actual).isEqualTo(expect);
//    }
//
//    @DisplayName("http request message body 를 inputstream 이 가지고 있습니다. ")
//    @ParameterizedTest
//    @MethodSource("provideMessage")
//    void test4(String request, String expect) throws IOException {
//        // given
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getBytes(UTF_8));
//        HttpRequest httpRequest = HttpRequest.parse(byteArrayInputStream);
//        BufferedInputStream bodyInputStream = httpRequest.getBodyInputStream();
//
//        Map<String, Set<String>> header = httpRequest.getHeader();
//        System.out.println(header);
//
//        //when
//        byte[] readBytes = bodyInputStream.readAllBytes();
//        String actual = new String(readBytes);
//
//        //then
//        Assertions.assertThat(actual).isEqualTo(expect);
//    }
//
//    private static Stream<Arguments> provideHttpRequest() {
//        return Stream.of(
//            Arguments.of("GET /doc/test.html HTTP/1.1\r\n" +
//                             "Host: www.test1.1.com\r\n" +
//                             "Accept : text/html, application/xhtml.xml\r\n" +
//                             "Accept-Language : en-us\r\n" +
//                             "\r\n" +
//                             "test body")
//        );
//    }
//
//    private static Stream<Arguments> provideHeader() {
//        return Stream.of(
//            Arguments.of("GET /doc/test.html HTTP/1.1\r\n" +
//                             "Host: www.test1.1.com\r\n" +
//                             "Accept : text/html, application/xhtml.xml\r\n" +
//                             "Accept-Language : en-us\r\n" +
//                             "\r\n" +
//                             "test body",
//                         Map.of("Host", Set.of("www.test1.1.com"),
//                                "Accept", Set.of("application/xhtml.xml", "text/html"),
//                                "Accept-Language", Set.of("en-us")
//                         )
//            )
//        );
//    }
//
//    private static Stream<Arguments> provideMessage() {
//        return Stream.of(
//            Arguments.of("GET /doc/test.html HTTP/1.1\r\n" +
//                             "Host: www.test1.1.com\r\n" +
//                             "Accept : text/html, application/xhtml.xml\r\n" +
//                             "Accept-Language : en-us\r\n" +
//                             "\r\n" +
//                             "test body",
//                         "test body"
//            )
//        );
//    }
//}