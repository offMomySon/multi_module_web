//package mapper;
//
//import java.util.stream.Stream;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import vo.HttpMethod;
//
//class HttpMethodUrlMatcherTest {
//    @DisplayName("http method 가 다르면 false 를 반환합니다.")
//    @Test
//    void test1() throws Exception {
//        //given
//        HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(vo.HttpMethod.GET, "/test");
//        HttpMethodUrlMatcher otherIndicator = new HttpMethodUrlMatcher(vo.HttpMethod.POST, "/test");
//
//        //when
//        boolean actual = httpMethodUrlMatcher.match(otherIndicator);
//
//        //then
//        Assertions.assertThat(actual).isFalse();
//    }
//
//    @DisplayName("http method 가 일치하고, url 이 일치하면 true 을 반환합니다.")
//    @ParameterizedTest
//    @MethodSource("provideEqualUrl")
//    void test2(String url, String otherUrl) throws Exception {
//        //given
//        HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(vo.HttpMethod.GET, url);
//        HttpMethodUrlMatcher otherHttpMethodUrlMatcher = new HttpMethodUrlMatcher(vo.HttpMethod.GET, otherUrl);
//
//        //when
//        boolean actual = httpMethodUrlMatcher.equals(otherHttpMethodUrlMatcher);
//
//        //then
//        Assertions.assertThat(actual).isTrue();
//    }
//
//    @DisplayName("http method 가 일치하고, url 이 일치하지 않으면 false 을 반환합니다.")
//    @ParameterizedTest
//    @MethodSource("provideDiffUrl")
//    void test3(String url, String otherUrl) throws Exception {
//        //given
//        HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(vo.HttpMethod.GET, url);
//        HttpMethodUrlMatcher otherHttpMethodUrlMatcher = new HttpMethodUrlMatcher(vo.HttpMethod.GET, otherUrl);
//
//        //when
//        boolean actual = httpMethodUrlMatcher.equals(otherHttpMethodUrlMatcher);
//
//        //then
//        Assertions.assertThat(actual).isFalse();
//    }
//
////    @DisplayName("http method 가 일치하고, url 이 일치하지 않으면 false 을 반환합니다.")
////    @Test
////    void test4() throws Exception {
////        //given
////        //when
////        Throwable actual = Assertions.catchThrowable(() -> {
////            MethodIndicator.from(vo.HttpMethod.GET, "/controllerUrl", "/methodUrl");
////        });
////
////        //then
////        Assertions.assertThat(actual).isNull();
////    }
//
//    private static Stream<Arguments> provideEqualUrl() {
//        return Stream.of(
//            Arguments.of("/test", "/test"),
//            Arguments.of("/test/depth1", "/test/depth1"),
//            Arguments.of("/test/depth1/depth2", "/test/depth1/depth2"),
//            Arguments.of("/test/depth1/depth2/depth3", "/test/depth1/depth2/depth3"),
//            Arguments.of("/test/depth1/depth2/depth3/depth4", "/test/depth1/depth2/depth3/depth4")
//        );
//    }
//
//    public static Stream<Arguments> provideDiffUrl() {
//        return Stream.of(
//            Arguments.of("/test/diffPath1", "/test/_diffPath1"),
//            Arguments.of("/test/diffLength", "/test"),
//            Arguments.of("/test/depth1/diffLength", "/test/depth1"),
//            Arguments.of("/test/depth1/depth2/diffLength", "/test/depth1/depth2"),
//            Arguments.of("/test/depth1/depth2/depth3/diffLength", "/test/depth1/depth2/depth3"),
//
//            Arguments.of("/test/{pathVariable}/diffLength", "/test/{pathVariable}"),
//            Arguments.of("/test/{pathVariable}/path1/{pv2}/diffLength", "/test/{pathVariable}/depth1/{pv2}"),
//            Arguments.of("/test/{pathVariable}/path1/{pv2}/path2/{pv3}/diffLength", "/test/{pathVariable}/depth1/{pv2}/path2/{pv3}")
//        );
//    }
//}