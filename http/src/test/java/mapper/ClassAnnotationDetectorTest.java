package mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ClassAnnotationDetectorTest {
    public static Stream<Arguments> provideAnnotation() {
        return Stream.of(
            Arguments.of(TestAnnotation.class, true),
            Arguments.of(UnUsedTestAnnotation.class, false),
            Arguments.of(TestClass.class, false)
        );
    }

    @DisplayName("class 가 가진 annotation 중에서 특정 어노테이션을 가지고 있는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideAnnotation")
    void test(Class<?> testAnnotation, boolean expect) throws Exception {
        //given
        Class<TestClass> testClass = TestClass.class;
        ClassAnnotationDetector classAnnotationDetector = new ClassAnnotationDetector(testClass);

        //when
        boolean actual = classAnnotationDetector.isAnnotatedOnClass(testAnnotation);

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("class 가 가진 annotation 조회시 , annotation class 가 아니면 false 를 반환합니다.")
    @Test
    void test2() throws Exception {
        //given
        Class<TestClass> testClazz = TestClass.class;
        ClassAnnotationDetector classAnnotationDetector = new ClassAnnotationDetector(testClazz);

        //when
        boolean actual = classAnnotationDetector.isAnnotatedOnClass(testClazz);

        //then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("class 의 method 중에서 특정 annotation 을 가진 methods 를 가져온다.")
    @Test
    void test1() {
        //given
        Class<TestClass> testClass = TestClass.class;
        ClassAnnotationDetector classAnnotationDetector = new ClassAnnotationDetector(testClass);

        Class<TestAnnotation> annotationClass = TestAnnotation.class;

        Set<Method> expect = Set.of(TestClass.getExistTestAnnotationMethod());

        //when
        Set<Method> methods = classAnnotationDetector.findMethod(annotationClass);

        //then
        Assertions.assertThat(methods).isEqualTo(expect);
    }

    @DisplayName("class 의 method 중에서 특정 annotation 을 가진 method 조회시, annotation class 가 아니면 비어있는 정보를 줍니다.")
    @Test
    void test() throws Exception {
        //given
        Class<TestClass> testClazz = TestClass.class;
        ClassAnnotationDetector classAnnotationDetector = new ClassAnnotationDetector(testClazz);

        //when
        Set<Method> actual = classAnnotationDetector.findMethod(testClazz);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @DisplayName("class 가 가진 annotation 중에서 특정 annotation 찾습니다.")
    @Test
    void test3() throws Exception {
        //given
        Class<TestClass> testClazz = TestClass.class;
        ClassAnnotationDetector classAnnotationDetector = new ClassAnnotationDetector(testClazz);

        Class<TestAnnotation> annotationClass = TestAnnotation.class;
        //when
        Optional<TestAnnotation> annotationOptional = classAnnotationDetector.findAnnotationOnClass(annotationClass);

        //then
        Assertions.assertThat(annotationOptional).isPresent();
    }

    @DisplayName("class 가 가진 annotation 중에서 특정 annotation 을 찾을때, annotation class 를 넘기지 않으면 empty 를 반환합니다.")
    @Test
    void test4() throws Exception {
        //given
        Class<TestClass> testClazz = TestClass.class;
        ClassAnnotationDetector classAnnotationDetector = new ClassAnnotationDetector(testClazz);

        //when
        Optional<TestClass> actual = classAnnotationDetector.findAnnotationOnClass(testClazz);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @DisplayName("class 의 method 가 가진 annotation 중에서 특정 annotation 을 찾습니다.")
    @Test
    void test5() throws Exception {
        //given
        Class<TestClass> testClazz = TestClass.class;
        ClassAnnotationDetector classAnnotationDetector = new ClassAnnotationDetector(testClazz);

        // TODO 중복 어노테이션 처리는 어떻게?
        //when
        Optional<TestAnnotation> actual = classAnnotationDetector.findAnnotationOnMethod(TestClass.getExistTestAnnotationMethod(), TestAnnotation.class);

        //then
        Assertions.assertThat(actual).isPresent();
    }

    @DisplayName("class 의 method 가 가진 annotation 중에서 특정 annotation 을 찾을떄, 인자가 annotation class 가 아니면 emtpy 를 반환합니다.")
    @Test
    void test6() throws Exception {
        //given
        Class<TestClass> testClazz = TestClass.class;
        ClassAnnotationDetector classAnnotationDetector = new ClassAnnotationDetector(testClazz);

        //when
        Optional<TestClass> actual = classAnnotationDetector.findAnnotationOnMethod(TestClass.getExistTestAnnotationMethod(), testClazz);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @DisplayName("class 의 method 가 가진 annotation 중에서 특정 annotation 을 찾을떄, 인자가 존재하지 않는 method 이면 empty 를 반환합니다.")
    @Test
    void test7() throws Exception {
        //given
        Class<TestClass> testClazz = TestClass.class;
        ClassAnnotationDetector classAnnotationDetector = new ClassAnnotationDetector(testClazz);

        //when
        Optional<TestClass> actual = classAnnotationDetector.findAnnotationOnMethod(TestAnotherClass.getAnotherMethod(), testClazz);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @TestAnnotation
    public static class TestClass {
        @TestAnnotation
        public void existTestAnnotationMethod() {

        }

        public static Method getExistTestAnnotationMethod() {
            try {
                return TestClass.class.getDeclaredMethod("existTestAnnotationMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @TestAnnotation
    public static class TestAnotherClass {
        @TestAnnotation
        public void existTestAnnotationMethod() {

        }

        public static Method getAnotherMethod() {
            try {
                return TestClass.class.getDeclaredMethod("existTestAnnotationMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface UnUsedTestAnnotation {
    }
}