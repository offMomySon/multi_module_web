package mapper;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnnotatedClassTest {

    @DisplayName("class 가 가지고 있는 annotation 중에서 특정 annotation 이 존재하면 true 를 반환합니다.")
    @Test
    void test1() throws Exception {
        //given
        AnnotatedClass annotatedClass = new AnnotatedClass(TestClass.class);

        //when
        boolean actual = annotatedClass.isAnnotated(TestAnnotation.class);

        //then
        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("class 가 가지고 있는 annotation 중에서 특정 annotation 이 존재하지 않으면 false 를 반환합니다.")
    @Test
    void test1_1() throws Exception {
        //given
        AnnotatedClass annotatedClass = new AnnotatedClass(TestClass.class);

        //when
        boolean actual = annotatedClass.isAnnotated(DoesNotUsedAnnotation.class);

        //then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("annotation 존재여부를 확인할 때, annotation class 를 전달하지 않으면 fasle 를 반환합니다.")
    @Test
    void test1_2() throws Exception {
        //given
        AnnotatedClass annotatedClass = new AnnotatedClass(TestClass.class);

        //when
        boolean actual = annotatedClass.isAnnotated(TestClass.class);

        //then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("class 가 가지고 있는 annotation 중에서 특정 annotation 을 찾습니다.")
    @Test
    void test2() throws Exception {
        //given
        AnnotatedClass annotatedClass = new AnnotatedClass(TestClass.class);

        //when
        Optional<TestAnnotation> actual = annotatedClass.find(TestAnnotation.class);

        //then
        Assertions.assertThat(actual).isPresent();
    }

    @DisplayName("class 가 가지고 있는 annotation 중에서 특정 annotation 존재하지 않으면 empty 를 반환합니다.")
    @Test
    void test2_1() throws Exception {
        //given
        AnnotatedClass annotatedClass = new AnnotatedClass(TestClass.class);

        //when
        Optional<DoesNotUsedAnnotation> actual = annotatedClass.find(DoesNotUsedAnnotation.class);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @DisplayName("class 가 가지고 있는 annotation 중에서 특정 annotation 찾을떄, 인자를 annotation class 이 아니면 empty 를 반환 합니다.")
    @Test
    void test2_2() throws Exception {
        //given
        AnnotatedClass annotatedClass = new AnnotatedClass(TestClass.class);

        //when
        Optional<TestClass> actual = annotatedClass.find(TestClass.class);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @TestAnnotation
    public static class TestClass {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface DoesNotUsedAnnotation {

    }
}