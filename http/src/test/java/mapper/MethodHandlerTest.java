package mapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.marker.RequestMapping;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vo.HttpMethod;

class MethodHandlerTest {

    @DisplayName("factory method 로 객체를 생성합니다.")
    @Test
    void test1() {
        //given
        Set<String> controllerUrls = TestController.getControllerUrls();
        Method method = TestController.getAnnotatedMethod();

        //when
        Throwable actual = Assertions.catchThrowable(() -> MethodHandler.from(controllerUrls, method));

        //then
        Assertions.assertThat(actual).isNull();
    }

    @DisplayName("factory method 로 객체 생성시, method 에 requestMapping 어노테이션이 존재하지 않으면 exception 이 발생합니다.")
    @Test
    void test2() throws Exception {
        //given
        Set<String> controllerUrls = TestController.getControllerUrls();
        Method method = TestController.getNotAnnotatedMethod();

        //when
        Throwable actual = Assertions.catchThrowable(() -> MethodHandler.from(controllerUrls, method));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("prefix url 과 method url, httpMethod 로 생성한 MethodIndicator 를 가진 객체를 생성합니다.")
    @Test
    void test3() throws Exception {
        //given
        Set<String> controllerUrls = TestController.getControllerUrls();
        Method method = TestController.getAnnotatedMethod();

        MethodHandler methodHandler = MethodHandler.from(controllerUrls, method);
        List<MethodIndicator> methodIndicators = TestController.getMethodIndicators();

        //when
        List<Boolean> actuals = methodIndicators.stream().map(methodHandler::isIndicated).collect(Collectors.toUnmodifiableList());

        //then
        Assertions.assertThat(actuals)
            .allSatisfy(actual -> Assertions.assertThat(actual).isTrue());
    }

    @DisplayName("일치하는 indicator 존재여부에 따라 값을 boolean 값을 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideIndicator")
    void test4(List<MethodIndicator> methodIndicators, MethodIndicator findIndicator, Boolean expect) {
        //given
        MethodHandler methodHandler = new MethodHandler(methodIndicators, TestController.getAnnotatedMethod());

        //when
        boolean actual = methodHandler.isIndicated(findIndicator);

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    public static class TestController {
        private static final String controllerUrl1 = "/controllerUrl1";
        private static final String controllerUrl2 = "/controllerUrl2";
        private static final String methodUrl1 = "/methodUrl1/";
        private static final String methodUrl2 = "/methodUrl2/";

        @RequestMapping(method = {HttpMethod.GET, HttpMethod.POST}, value = {methodUrl1, methodUrl2})
        public void annotatedMethod() {

        }

        public void notAnnotatedMethod() {

        }

        public static Set<String> getControllerUrls() {
            return Set.of(controllerUrl1, controllerUrl2);
        }

        public static Set<String> getMethodUrls() {
            return Set.of(methodUrl1, methodUrl2);
        }

        public static Set<HttpMethod> getHttpMethod() {
            return Set.of(HttpMethod.GET, HttpMethod.POST);
        }

        public static List<MethodIndicator> getMethodIndicators() {
            String methodUri1 = controllerUrl1 + methodUrl1;
            String methodUri2 = controllerUrl1 + methodUrl2;
            String methodUri3 = controllerUrl2 + methodUrl1;
            String methodUri4 = controllerUrl2 + methodUrl2;

            List<MethodIndicator> methodIndicators = new ArrayList<>();
            methodIndicators.add(new MethodIndicator(HttpMethod.GET, methodUri1));
            methodIndicators.add(new MethodIndicator(HttpMethod.GET, methodUri2));
            methodIndicators.add(new MethodIndicator(HttpMethod.GET, methodUri3));
            methodIndicators.add(new MethodIndicator(HttpMethod.GET, methodUri4));
            methodIndicators.add(new MethodIndicator(HttpMethod.POST, methodUri1));
            methodIndicators.add(new MethodIndicator(HttpMethod.POST, methodUri2));
            methodIndicators.add(new MethodIndicator(HttpMethod.POST, methodUri3));
            methodIndicators.add(new MethodIndicator(HttpMethod.POST, methodUri4));
            return Collections.unmodifiableList(methodIndicators);
        }

        public static Method getNotAnnotatedMethod() {
            try {
                return TestController.class.getDeclaredMethod("notAnnotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Method getAnnotatedMethod() {
            try {
                return TestController.class.getDeclaredMethod("annotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Stream<Arguments> provideIndicator() {
        return Stream.of(
            Arguments.of(
                List.of(
                    new MethodIndicator(HttpMethod.GET, "/methodUri"),
                    new MethodIndicator(HttpMethod.GET, "/methodUri")
                ),
                new MethodIndicator(HttpMethod.GET, "/methodUri"),
                Boolean.TRUE
            ),
            Arguments.of(
                List.of(
                    new MethodIndicator(HttpMethod.GET, "/methodUri1"),
                    new MethodIndicator(HttpMethod.GET, "/methodUri2")
                ),
                new MethodIndicator(HttpMethod.GET, "/methodUri"),
                Boolean.FALSE
            )
        );
    }

}
