package mapper;

import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vo.HttpMethod;

class TaskIndicatorTest {
    @DisplayName("http method 가 다르면 false 를 반환합니다.")
    @Test
    void test1() throws Exception {
        //given
        TaskIndicator taskIndicator = new TaskIndicator(HttpMethod.GET, "/test");
        TaskIndicator otherIndicator = new TaskIndicator(HttpMethod.POST, "/test");

        //when
        boolean actual = taskIndicator.equals(otherIndicator);

        //then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("http method 가 일치하고, url 이 일치하면 true 을 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideEqualUrl")
    void test2(String url, String otherUrl) throws Exception {
        //given
        TaskIndicator taskIndicator = new TaskIndicator(HttpMethod.GET, url);
        TaskIndicator otherTaskIndicator = new TaskIndicator(HttpMethod.GET, otherUrl);

        //when
        boolean actual = taskIndicator.equals(otherTaskIndicator);

        //then
        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("")
    @Test
    void test() throws Exception {
        //given
        String s = Optional.of("hello world.")
            .map(h -> h+ "test")
            .map(h-> h + "dept1")
            .map(h-> h + "dept2")
            .map(h -> h + "hello world.")
            .orElseThrow(()-> new RuntimeException("dsafsd"));

        System.out.println(s);

        String data = null;
        String s1 = Optional.ofNullable(data).orElseGet(()->"sdfdas");
        System.out.println(s1);

        Optional<Object> empty = Optional.empty();
        System.out.println(empty);

        //when

        //then

    }


    @DisplayName("http method 가 일치하고, url 이 일치하지 않으면 false 을 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideDiffUrl")
    void test3(String url, String otherUrl) throws Exception {
        //given
        TaskIndicator taskIndicator = new TaskIndicator(HttpMethod.GET, url);
        TaskIndicator otherTaskIndicator = new TaskIndicator(HttpMethod.GET, otherUrl);

        //when
        boolean actual = taskIndicator.equals(otherTaskIndicator);

        //then
        Assertions.assertThat(actual).isFalse();
    }

    private static Stream<Arguments> provideEqualUrl() {
        return Stream.of(
            Arguments.of("/test", "/test"),
            Arguments.of("/test/depth1", "/test/depth1"),
            Arguments.of("/test/depth1/depth2", "/test/depth1/depth2"),
            Arguments.of("/test/depth1/depth2/depth3", "/test/depth1/depth2/depth3"),
            Arguments.of("/test/depth1/depth2/depth3/depth4", "/test/depth1/depth2/depth3/depth4")
        );
    }

    public static Stream<Arguments> provideDiffUrl() {
        return Stream.of(
            Arguments.of("/test/diffPath1", "/test/_diffPath1"),
            Arguments.of("/test/diffLength", "/test"),
            Arguments.of("/test/depth1/diffLength", "/test/depth1"),
            Arguments.of("/test/depth1/depth2/diffLength", "/test/depth1/depth2"),
            Arguments.of("/test/depth1/depth2/depth3/diffLength", "/test/depth1/depth2/depth3"),

            Arguments.of("/test/{pathVariable}/diffLength", "/test/{pathVariable}"),
            Arguments.of("/test/{pathVariable}/path1/{pv2}/diffLength", "/test/{pathVariable}/depth1/{pv2}"),
            Arguments.of("/test/{pathVariable}/path1/{pv2}/path2/{pv3}/diffLength", "/test/{pathVariable}/depth1/{pv2}/path2/{pv3}")
        );
    }
}