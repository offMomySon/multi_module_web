//package request;
//
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Stream;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//import static org.junit.jupiter.api.Assertions.*;
//
//
//class HttpRequestTest {
//
//    @ParameterizedTest
//    @MethodSource("provideWrongRequestLine")
//    public void test(String request){
//        //given
//        //when
//        Throwable actual = Assertions.catchThrowable(() -> HttpRequest.of(request));
//
//        //then
//        Assertions.assertThat(actual)
//            .isNotNull();
//    }
//
//    private static Stream<String> provideWrongRequestLine(){
//        String value = "GET /hello?age=30 HTTP/1.1\r\n" +
//            "Host: localhost:8080\r\n" +
//            "User-Agent: curl/7.85.0\r\n" +
//            "Accept: */*\r\n" +
//            "\r\n";
//
//        return Stream.of(value);
//    }
//
//
//}