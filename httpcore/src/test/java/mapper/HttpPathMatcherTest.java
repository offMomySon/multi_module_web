//package mapper;
//
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Stream;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.junit.jupiter.params.provider.MethodSource;
//import vo.HttpMethod;
//
//class HttpPathMatcherTest {
//
//    @DisplayName("")
//    @ParameterizedTest
//    @CsvSource({
//        "/test, /test, true",
//        "/test/path1, /test/path1, true",
//        "/test/**, /test/path1, true",
//        "/test/**, /test/path1/path2, true",
//        "/test/**/test2, /test/path1/test2, true",
//        "/test/**/test2, /test/path1/path2/test2, true",
//        "/test/**/test2/**, /test/path1/path2/test3/test3, false",
//        "/test/test2, /test/test2/test3/.., true",
//        "/test/test2, /test/test2/../test2, true",
//        "/test/test2, /test/test2/test3/../test4/../test5/.., true",
//        "/test/**, /test/test2/.., true",
//        "/test/**, /test/test2/../test3, true",
//        "/test/**/test2, /test/test2/../test3, false",
//        "/test/{pv1}/test2, /test/pv1/test2/, true",
//        "/test/{pv1}/test2, /test/pv1/pv2/test2/, false",
//        "/test/{pv1}/**/test2, /test/pv1/wildcar1/wildcar2/test2/, true",
//        "/test/{pv1}/test2/**/test3, /test/pv1/test2/wildcar2/test2/, false",
//    })
//    void test(String registerPath, String requestPath, boolean expect) throws Exception {
//        //given
//        HttpPathMatcher resolver = new HttpPathMatcher(HttpMethod.GET, registerPath, MethodDefClass.class.getMethod("method"));
//
//        //when
//        boolean actual = resolver.match(HttpMethod.GET, requestPath).isPresent();
//
//        //then
//        Assertions.assertThat(actual)
//            .isEqualTo(expect);
//    }
//
//    @DisplayName("")
//    @ParameterizedTest
//    @MethodSource("provideResult")
//    void test(String registerPath, String requestPath, Map<String, String> expectPathVariables) throws Exception {
//        //given
//        HttpPathMatcher resolver = new HttpPathMatcher(HttpMethod.GET, registerPath, MethodDefClass.class.getMethod("method"));
//
//        //when
//        Optional<HttpPathMatcher.Result> optionalResult = resolver.match(HttpMethod.GET, requestPath);
//
//        //then
//        Assertions.assertThat(optionalResult).isPresent();
//        HttpPathMatcher.Result result = optionalResult.get();
//        Assertions.assertThat(result.getPathVariables()).isEqualTo(expectPathVariables);
//    }
//
//    public static Stream<Arguments> provideResult() {
//        return Stream.of(
//            Arguments.of("/test/{path}", "/test/value1", Map.of("path", "value1")),
//            Arguments.of("/test/{path}/**/{path2}/test2", "/test/value1/1/2/3/4/value2/test2", Map.of("path", "value1", "path2", "value2"))
//        );
//    }
//
//
//    public static class MethodDefClass {
//
//        public void method() {
//
//        }
//    }
//
//
//}