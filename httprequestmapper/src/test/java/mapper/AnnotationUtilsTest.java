package mapper;

import java.util.Arrays;
import java.util.stream.Stream;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import mapper.marker.RequestParam;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AnnotationUtilsTest {

    @DisplayName("모든 annotation 을 가지고 있으면 true 를 반환합니다.")
    @Test
    void test0() throws Exception {
        //given
        Class<AnnotatedClass> clazz = AnnotatedClass.class;
        Class<?>[] annotationClazzes = AnnotatedClass.getAllAnnotations();

        //when
        boolean actual = AnnotationUtils.existAll(clazz, annotationClazzes);

        //then
        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("모든 annotation 을 가지고 있지 않으면 false 를 반환합니다.")
    @Test
    void test1() throws Exception {
        //given
        Class<AnnotatedClass> clazz = AnnotatedClass.class;
        Class<?>[] annotationClazzes = AnnotatedClass.getOverboardAnnotations();

        //when
        boolean actual = AnnotationUtils.existAll(clazz, annotationClazzes);

        //then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("annotation class 배열이 null, 빈배열이면 excepitonl 이 발생합니다.")
    @ParameterizedTest
    @MethodSource("getEmtpyParams")
    void test2(Class<AnnotatedClass> clazz, Class<?>[] annotationClazzes) throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> AnnotationUtils.existAll(clazz, annotationClazzes));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    public static Stream<Arguments> getEmtpyParams() {
        return Stream.of(
            Arguments.of(null, AnnotatedClass.getEmptyAnnotations()),
            Arguments.of(null, AnnotatedClass.getAllAnnotations()),
            Arguments.of(AnnotatedClass.class, null),
            Arguments.of(AnnotatedClass.class, AnnotatedClass.getEmptyAnnotations())
        );
    }

    @Controller
    @RequestMapping
    private static class AnnotatedClass {

        public void annotatedMethod() {
        }

        public static Class<?>[] getAllAnnotations() {
            return new Class[]{Controller.class, RequestMapping.class};
        }

        public static Class<?>[] getOverboardAnnotations() {
            return new Class[]{Controller.class, RequestMapping.class, RequestParam.class};
        }

        public static Class<?>[] getEmptyAnnotations() {
            return new Class[]{null, null};
        }

        public static void test(Class<?>... a) {
            Class<?>[] a1 = a;
            System.out.println(Arrays.toString(a1));
        }
    }
}