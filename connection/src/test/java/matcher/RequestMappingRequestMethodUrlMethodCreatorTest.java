package matcher;

import annotation.RequestMapping;
import java.lang.reflect.Method;
import java.util.List;
import matcher.creator.RequestMappingValueExtractor;
import matcher.creator.RequestMappingValueExtractor.RequestMappedMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import web.RequestMethod;

class RequestMappingRequestMethodUrlMethodCreatorTest {

    @DisplayName("class 의 method 중에서 requestMapping 어노테이션이 붙지 않은 method 를 받으면 empty 를 반환합니다.")
    @Test
    void test1() throws Exception {
        //given
        Class<TestAnnotatedClass> clazz = TestAnnotatedClass.class;
        RequestMappingValueExtractor valueExtractor = new RequestMappingValueExtractor(clazz);
        Method method = TestAnnotatedClass.getDoesNotAnnotatedMethod();

        //when
        List<RequestMappedMethod> actual = valueExtractor.extractRequestMappedMethods(method);

        //then
        Assertions.assertThat(actual)
            .isEmpty();
    }

    @DisplayName("class 의 RequestMapping urls, method 의 RequestMapping urls, methods 의 카타시안곱 으로 HttpMethodUrlMethod 를 생성합니다.")
    @Test
    void test2() throws Exception {
        //given
        Class<TestAnnotatedClass> clazz = TestAnnotatedClass.class;
        RequestMappingValueExtractor valueExtractor = new RequestMappingValueExtractor(clazz);
        Method method = TestAnnotatedClass.getAnnotatedMethod();

        List<RequestMappedMethod> expect = TestAnnotatedClass.getCartesianProduct();

        //when
        List<RequestMappedMethod> actual = valueExtractor.extractRequestMappedMethods(method);

        //then

        Assertions.assertThat(actual)
            .containsAll(expect);
    }


    @RequestMapping(value = {"/testclass1", "/testclass2"})
    private static class TestAnnotatedClass {

        @RequestMapping(value = {"/testMethod1", "/testMethod2"}, method = {RequestMethod.GET, RequestMethod.POST})
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

        public static List<RequestMappedMethod> getCartesianProduct() {
            Method annotatedMethod = getAnnotatedMethod();
            return List.of(
                new RequestMappedMethod(RequestMethod.GET, "/testclass1" + "/testMethod1", annotatedMethod),
                new RequestMappedMethod(RequestMethod.GET, "/testclass1" + "/testMethod2", annotatedMethod),
                new RequestMappedMethod(RequestMethod.GET, "/testclass2" + "/testMethod1", annotatedMethod),
                new RequestMappedMethod(RequestMethod.GET, "/testclass2" + "/testMethod2", annotatedMethod),
                new RequestMappedMethod(RequestMethod.POST, "/testclass1" + "/testMethod1", annotatedMethod),
                new RequestMappedMethod(RequestMethod.POST, "/testclass1" + "/testMethod2", annotatedMethod),
                new RequestMappedMethod(RequestMethod.POST, "/testclass2" + "/testMethod1", annotatedMethod),
                new RequestMappedMethod(RequestMethod.POST, "/testclass2" + "/testMethod2", annotatedMethod)
            );
        }
    }

    private static class TestDoesNotAnnotatedClass {
        @RequestMapping(value = {"/testMethod1", "/testMethod2"}, method = {RequestMethod.GET, RequestMethod.POST})
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