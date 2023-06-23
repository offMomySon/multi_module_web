package com.main.executor.util;

import com.main.container.annotation.Controller;
import com.main.container.util.AnnotationUtils;
import com.main.matcher.annotation.RequestMapping;
import com.main.matcher.annotation.RequestParam;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;
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
        Class<?>[] annotationClazzes = AnnotatedClass.getClassAnnotations();

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
        Class<?>[] annotationClazzes = AnnotatedClass.getOverboardClassAnnotations();

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

    @DisplayName("peekMethod 의 class param 이 null, empty 이면 exception 이 발생합니다.")
    @ParameterizedTest
    @MethodSource("getEmtpyParams")
    void test3(Class<AnnotatedClass> clazz, Class<?>[] annotationClazzes) throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> AnnotationUtils.peekMethods(clazz, annotationClazzes));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("모든 annotation 을 가지고 있는 methods 를 가져옵니다.")
    @Test
    void test4() throws Exception {
        //given
        Class<?> clazz = AnnotatedClass.class;
        Class<?>[] methodAnnotations = AnnotatedClass.getMethodAnnotations();

        //when
        List<Method> actual = AnnotationUtils.peekMethods(clazz, methodAnnotations);

        System.out.println(actual);
        System.out.println(AnnotatedClass.getAnnotatedMethod());

        //then
        Assertions.assertThat(actual)
            .contains(AnnotatedClass.getAnnotatedMethod());
    }

    @DisplayName("모든 annotation 을 가지고 있지 못하면 method 를 가져오지 못합니다.")
    @Test
    void test5() throws Exception {
        //given
        Class<?> clazz = AnnotatedClass.class;
        Class<?>[] methodAnnotations = AnnotatedClass.getOverBoardMethodAnnotations();

        //when
        List<Method> actual = AnnotationUtils.peekMethods(clazz, methodAnnotations);

        //then
        Assertions.assertThat(actual)
            .doesNotContain(AnnotatedClass.getAnnotatedMethod(),
                            AnnotatedClass.getDoesNotAnnotatedMethod());
    }

    private static Stream<Arguments> getEmtpyParams() {
        return Stream.of(
            Arguments.of(null, AnnotatedClass.getEmptyAnnotations()),
            Arguments.of(null, AnnotatedClass.getClassAnnotations()),
            Arguments.of(AnnotatedClass.class, null),
            Arguments.of(AnnotatedClass.class, AnnotatedClass.getEmptyAnnotations())
        );
    }

    @Controller
    @RequestMapping
    private static class AnnotatedClass {
        @MethodAnnotation1
        @MethodAnnotation2
        public void annotatedMethod() {
        }

        public void doesNotAnnotatedMethod() {
        }

        public static Method getAnnotatedMethod() {
            try {
                return AnnotatedClass.class.getMethod("annotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Method getDoesNotAnnotatedMethod() {
            try {
                return AnnotatedClass.class.getMethod("doesNotAnnotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        private static Class<?>[] getMethodAnnotations() {
            return new Class[]{MethodAnnotation1.class, MethodAnnotation2.class};
        }

        private static Class<?>[] getOverBoardMethodAnnotations() {
            return new Class[]{MethodAnnotation1.class, MethodAnnotation2.class, MethodAnnotation3.class};
        }

        private static Class<?>[] getClassAnnotations() {
            return new Class[]{Controller.class, RequestMapping.class};
        }

        private static Class<?>[] getOverboardClassAnnotations() {
            return new Class[]{Controller.class, RequestMapping.class, RequestParam.class};
        }

        private static Class<?>[] getEmptyAnnotations() {
            return new Class[]{null, null};
        }
    }

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MethodAnnotation1 {
    }

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MethodAnnotation2 {
    }

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MethodAnnotation3 {
    }
}