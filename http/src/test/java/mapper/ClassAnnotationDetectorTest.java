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

    @DisplayName("class 에 설정된 annotation 존재 여부를 확인한다.")
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

    @DisplayName("특정 어노테이션에 해당하는 method 를 가져온다.")
    @Test
    void test1() {
        //given
        Class<TestClass> testClass = TestClass.class;
        ClassAnnotationDetector classAnnotationDetector = new ClassAnnotationDetector(testClass);

        Class<TestAnnotation> annotationClass = TestAnnotation.class;

        Set<Method> expect = Set.of(TestClass.getExistTestAnnotationMethod());

        //when
        Set<Method> methods = classAnnotationDetector.findAnnotatedMethods(annotationClass);

        //then
        Assertions.assertThat(methods).isEqualTo(expect);
    }

    @DisplayName("annotation 존재 여부를 확인시, annotation class 가 아니면 false 를 반환합니다.")
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

    //    있는 경우없는 경우 테케.
    @DisplayName("특정 어노테이션을 조회합니다.")
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

    @TestAnnotation
    public static class TestClass {

        public void absentTestAnnotationMethod() {

        }

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

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface UnUsedTestAnnotation {
    }

//    class, method
//      controller, requestMapping, -> annotation 찾기.
//    @DisplayName("class 에 설정된 annotation 존재 여부를 확인한다.")
//    @DisplayName("class 의 method 가")

}