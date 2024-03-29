package main.matcher;

import annotation.RequestMapping;
import java.lang.reflect.Method;
import java.util.List;
import matcher.RequestMethod;
import matcher.creator.EndPointMethodInfo;
import matcher.creator.RequestMappingValueExtractor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingRequestMethodUrlMethodCreatorTest {

    @DisplayName("class 의 method 중에서 requestMapping 어노테이션이 붙지 않은 method 를 받으면 empty 를 반환합니다.")
    @Test
    void test1() throws Exception {
        //given
        Class<TestAnnotatedClass> clazz = TestAnnotatedClass.class;
        RequestMappingValueExtractor valueExtractor = new RequestMappingValueExtractor(clazz);
        Method method = TestAnnotatedClass.getDoesNotAnnotatedMethod();

        //when
        List<EndPointMethodInfo> actual = valueExtractor.extractRequestMappedMethods(method);

        //then
        Assertions.assertThat(actual)
            .isEmpty();
    }

//    @DisplayName("class 의 RequestMapping urls, method 의 RequestMapping urls, methods 의 카타시안곱 으로 HttpMethodUrlMethod 를 생성합니다.")
//    @Test
//    void test2() throws Exception {
//        //given
//        Class<TestAnnotatedClass> clazz = TestAnnotatedClass.class;
//        RequestMappingValueExtractor valueExtractor = new RequestMappingValueExtractor(clazz);
//        Method method = TestAnnotatedClass.getAnnotatedMethod();
//
//        List<EndPointMethodInfo> expect = TestAnnotatedClass.getCartesianProduct();
//
//        //when
//        List<EndPointMethodInfo> actual = valueExtractor.extractRequestMappedMethods(method);
//
//        //then
//
//        Assertions.assertThat(actual)
//            .containsAll(expect);
//    }


    private static class TestAnnotatedClass {

        @RequestMapping(url = {"/testMethod1", "/testMethod2"}, method = {RequestMethod.GET, RequestMethod.POST})
        public void annotatedMethod() {
        }

        public void notAnnotatedMethod() {

        }

        public static Method getAnnotatedMethod() {
            try {
                return TestAnnotatedClass.class.getMethod("annotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Method getDoesNotAnnotatedMethod() {
            try {
                return TestAnnotatedClass.class.getMethod("notAnnotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static List<EndPointMethodInfo> getCartesianProduct() {
            Method annotatedMethod = getAnnotatedMethod();
            return List.of(
                new EndPointMethodInfo(RequestMethod.GET, "/testclass1" + "/testMethod1", null, annotatedMethod),
                new EndPointMethodInfo(RequestMethod.GET, "/testclass1" + "/testMethod2", null, annotatedMethod),
                new EndPointMethodInfo(RequestMethod.GET, "/testclass2" + "/testMethod1", null, annotatedMethod),
                new EndPointMethodInfo(RequestMethod.GET, "/testclass2" + "/testMethod2", null, annotatedMethod),
                new EndPointMethodInfo(RequestMethod.POST, "/testclass1" + "/testMethod1", null, annotatedMethod),
                new EndPointMethodInfo(RequestMethod.POST, "/testclass1" + "/testMethod2", null, annotatedMethod),
                new EndPointMethodInfo(RequestMethod.POST, "/testclass2" + "/testMethod1", null, annotatedMethod),
                new EndPointMethodInfo(RequestMethod.POST, "/testclass2" + "/testMethod2", null, annotatedMethod)
            );
        }
    }

    private static class TestDoesNotAnnotatedClass {
        @RequestMapping(url = {"/testMethod1", "/testMethod2"}, method = {RequestMethod.GET, RequestMethod.POST})
        public void annotatedMethod() {

        }

        public static Method getAnnotatedMethod() {
            try {
                return TestDoesNotAnnotatedClass.class.getMethod("annotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

}