package mapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.marker.Controller;
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
//        Set<String> controllerUrls = TestController.getControllerUrls();
        Class<TestController> clazz = TestController.class;
        Method method = TestController.getAnnotatedMethod();

        //when
        Throwable actual = Assertions.catchThrowable(() -> MethodHandler.from(clazz, method));

        //then
        Assertions.assertThat(actual).isNull();
    }

    @DisplayName("factory method 로 객체 생성시, method 에 requestMapping 어노테이션이 존재하지 않으면 exception 이 발생합니다.")
    @Test
    void test2() throws Exception {
        //given
        Class<TestController> clazz = TestController.class;
        Method method = TestController.getNotAnnotatedMethod();

        //when
        Throwable actual = Assertions.catchThrowable(() -> MethodHandler.from(clazz, method));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("prefix url 과 method url, httpMethod 로 생성한 MethodIndicator 를 가진 객체를 생성합니다.")
    @Test
    void test3() throws Exception {
        //given
        Class<TestController> clazz = TestController.class;
        Method method = TestController.getAnnotatedMethod();

        MethodHandler methodHandler = MethodHandler.from(clazz, method);
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
        Class<TestController> clazz = TestController.class;
        Method method = TestController.getAnnotatedMethod();
        MethodHandler methodHandler = new MethodHandler(methodIndicators, clazz, method);

        //when
        boolean actual = methodHandler.isIndicated(findIndicator);

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @Controller
    @RequestMapping(value = {"/controllerUrl1", "/controllerUrl2"})
    public static class TestController {
        private static final String methodUrl1 = "/methodUrl1/";
        private static final String methodUrl2 = "/methodUrl2/";

        @RequestMapping(method = {HttpMethod.GET, HttpMethod.POST}, value = {methodUrl1, methodUrl2})
        public void annotatedMethod() {

        }

        public void notAnnotatedMethod() {

        }

        public static Set<String> getControllerUrls() {
            RequestMapping classRequestMapping = AnnotationUtils.find(TestController.class, RequestMapping.class)
                .orElseThrow(() -> new RuntimeException("annotation not exist."));

            return Arrays.stream(classRequestMapping.value()).collect(Collectors.toUnmodifiableSet());
        }

        public static Set<String> getMethodUrls() {
            Method annotatedMethod = getAnnotatedMethod();
            RequestMapping methodRequestMapping = AnnotationUtils.find(annotatedMethod, RequestMapping.class)
                .orElseThrow(() -> new RuntimeException("annotation not exist."));

            return Arrays.stream(methodRequestMapping.value()).collect(Collectors.toUnmodifiableSet());
        }

        public static Set<HttpMethod> getHttpMethod() {
            Method annotatedMethod = getAnnotatedMethod();
            RequestMapping methodRequestMapping = AnnotationUtils.find(annotatedMethod, RequestMapping.class)
                .orElseThrow(() -> new RuntimeException("annotation not exist."));

            return Arrays.stream(methodRequestMapping.method()).collect(Collectors.toUnmodifiableSet());
        }

        public static List<MethodIndicator> getMethodIndicators() {
            Set<String> controllerUrls = getControllerUrls();
            Set<String> methodUrls = getMethodUrls();
            Set<HttpMethod> methods = getHttpMethod();

            Set<String> appendedMethodUrls = controllerUrls.stream()
                .flatMap(controllerUrl -> methodUrls.stream().map(methodUrl -> controllerUrl + methodUrl))
                .collect(Collectors.toUnmodifiableSet());

            return appendedMethodUrls.stream()
                .flatMap(methodUrl-> methods.stream().map(method -> new MethodIndicator(method, methodUrl)))
                .collect(Collectors.toUnmodifiableList());
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
