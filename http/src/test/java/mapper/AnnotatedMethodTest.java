package mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnnotatedMethodTest {

    @DisplayName("method 가 가지고있는 annotation 중에서 특정 annotation 를 소유하고 있으면 true 을 반환합니다.")
    @Test
    void test1() throws Exception {
        //given
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(TestClass.givenTestAnnotatedMethod());

        //when
        boolean actual = annotatedMethod.isAnnotated(UsedAnnotation.class);

        //then
        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("method 가 가지고있는 annotation 중에서 특정 annotation 를 소유하지 않으면 false 를 반환합니다.")
    @Test
    void test1_1() throws Exception {
        //given
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(TestClass.givenTestAnnotatedMethod());

        //when
        boolean actual = annotatedMethod.isAnnotated(DoesNotUsedAnnotation.class);

        //then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("method 가 가지고 있는 annotation 중에서 특정 annotation 소유를 확인할 때, annotation class 를 인자로 사용하지 않으면 falase 를 반환합니다.")
    @Test
    void test1_2() throws Exception {
        //given
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(TestClass.givenTestAnnotatedMethod());

        //when
        boolean actual = annotatedMethod.isAnnotated(TestClass.class);

        //then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("method 가 소유한 annotaion 중 특정 annotation 을 찾습니다.")
    @Test
    void test2_1() throws Exception {
        //given
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(TestClass.givenTestAnnotatedMethod());

        //when
        Optional<UsedAnnotation> actual = annotatedMethod.find(UsedAnnotation.class);


        //then
        Assertions.assertThat(actual).isPresent();
    }

    @DisplayName("method 가 소유한 annotation 중 특정 annotaion 을 찾을때 존재하지 않으면 empty 를 반환합니다.")
    @Test
    void test2_2() throws Exception {
        //given
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(TestClass.givenTestAnnotatedMethod());

        //when
        Optional<DoesNotUsedAnnotation> actual = annotatedMethod.find(DoesNotUsedAnnotation.class);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @DisplayName("method 가 소유한 annotation 중 특정 annotation 을 찾을때, 인자를 annotation class 로 전달하지 않으면 empty 를 반환합니다.")
    @Test
    void test2_3() throws Exception {
        //given
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(TestClass.givenTestAnnotatedMethod());

        //when
        Optional<TestClass> actual = annotatedMethod.find(TestClass.class);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    public static class TestClass {

        @UsedAnnotation
        public void testMethod() {

        }

        public static Method givenTestAnnotatedMethod() {
            try {
                return TestClass.class.getDeclaredMethod("testMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface UsedAnnotation {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface DoesNotUsedAnnotation {

    }
}