package mapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vo.HttpMethod;

class MethodHandlerRepositoryTest {

    @DisplayName("MethodHandlerRepository 를 생성합니다.")
    @Test
    void test1() throws Exception {
        //given
        Class<DoesNotControllerAnnotatedClass> clazz = DoesNotControllerAnnotatedClass.class;

        //when
        Throwable actual = Assertions.catchThrowable(() -> MethodHandlerRepository.from(List.of(clazz)));

        //then
        Assertions.assertThat(actual).isNull();
    }

    @DisplayName("controller 어노테이션이 달리지않은 class 로 객체를 생성하면, exception 이 발생합니다.")
    @Test
    void test2() throws Exception {
        //given
        Class<DoesNotControllerAnnotatedClass> clazz = DoesNotControllerAnnotatedClass.class;

        //when
        Throwable actual = Assertions.catchThrowable(() -> MethodHandlerRepository.from(List.of(clazz)));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("requestMapping 어노테이션이 붙지않은 class 로 생성하면, exception 이 발생합니다.")
    @Test
    void test3() throws Exception {
        //given
        Class<DoesNotRequestAnnotatedClass> clazz = DoesNotRequestAnnotatedClass.class;

        //when
        Throwable actual = Assertions.catchThrowable(() -> MethodHandlerRepository.from(List.of(clazz)));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("MethodIndicator 가 가리키는 MethodHandler 가 존재하면 반환합니다.")
    @Test
    void test4() throws Exception {
        //given
        Class<?> clazz = TestController.class;
        MethodHandlerRepository methodHandlerRepository = MethodHandlerRepository.from(List.of(clazz));
        List<MethodIndicator> methodIndicators = TestController.getMethodIndicators();

        //when
        Optional<MethodHandler> actual = methodHandlerRepository.find(methodIndicators.get(0));

        //then
        Assertions.assertThat(actual).isPresent();
    }

    @DisplayName("MethodIndicator 가 가리키는 MethodHandler 가 존재하지 않으면 빈값을 반환합니다.")
    @Test
    void test5() throws Exception {
        //given
        Class<?> clazz = TestController.class;
        MethodHandlerRepository repository = MethodHandlerRepository.from(List.of(clazz));
        MethodIndicator doesNotIndicateTestMethodIndicator = TestController.getDoesNotIndicateTestMethodIndicator();

        //when
        Optional<MethodHandler> actual = repository.find(doesNotIndicateTestMethodIndicator);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @Controller
    public static class DoesNotRequestAnnotatedClass {

        @RequestMapping(method = {HttpMethod.GET, HttpMethod.POST}, value = {"/methodUrl1", "/methodUrl2"})
        public void annotatedMethod() {

        }

        public void notAnnotatedMethod() {

        }
    }

    @RequestMapping(value = {"/controllerUrl1", "/controllerUrl2"})
    public static class DoesNotControllerAnnotatedClass {

        @RequestMapping(method = {HttpMethod.GET, HttpMethod.POST}, value = {"/methodUrl1", "/methodUrl2"})
        public void annotatedMethod() {

        }

        public void notAnnotatedMethod() {

        }
    }

    @Controller
    @RequestMapping(value = {"/controllerUrl1", "/controllerUrl2"})
    public static class TestController {

        @RequestMapping(method = {HttpMethod.GET, HttpMethod.POST}, value = {"/methodUrl1", "/methodUrl2"})
        public void annotatedMethod() {

        }

        public void notAnnotatedMethod() {

        }

        public static Set<String> getControllerUrls() {
            RequestMapping classRequestMapping = AnnotationUtils.find(MethodHandlerTest.TestController.class, RequestMapping.class)
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

        public static MethodIndicator getDoesNotIndicateTestMethodIndicator(){
            return new MethodIndicator(HttpMethod.GET, "/doesNotIndicate");
        }

        public static Method getNotAnnotatedMethod() {
            try {
                return MethodHandlerTest.TestController.class.getDeclaredMethod("notAnnotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Method getAnnotatedMethod() {
            try {
                return MethodHandlerTest.TestController.class.getDeclaredMethod("annotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }


}