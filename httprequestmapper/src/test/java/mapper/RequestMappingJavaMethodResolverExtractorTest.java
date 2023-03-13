package mapper;

import java.lang.reflect.Method;
import java.util.List;
import mapper.marker.RequestMapping;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vo.HttpMethod;

class RequestMappingJavaMethodResolverExtractorTest {

    @DisplayName("class 로 부터 HttpMethodUrlMethodResolver 를 추출합니다.")
    @Test
    void test0() throws Exception {
        //given
        Class<?> clazz = DoesNotHaveAnnotatedMethodClass.class;
        List<HttpMethodUrlMatcher> httpMethodUrlMethodResolvers = HaveAnnotatedMethodClass.getHttpMethodUrlMethodResolvers();

        //when
        List<JavaMethodResolver> actuals = RequestMappingHttpMethodUrlMethodResolverExtractor.extract(clazz);

        //then
        Assertions.assertThat(actuals)
            .allMatch(actual -> {
                for (HttpMethodUrlMatcher matcher : httpMethodUrlMethodResolvers) {
                    if (actual.resolve(matcher).isPresent()) {
                        return true;
                    }
                }
                return false;
            });
    }

    @DisplayName("requestMapping 이 존재하는 method 가 없으면 empty 를 반환합니다.")
    @Test
    void test1() throws Exception {
        //given
        Class<?> clazz = DoesNotHaveAnnotatedMethodClass.class;

        //when
        List<JavaMethodResolver> actuals = RequestMappingHttpMethodUrlMethodResolverExtractor.extract(clazz);

        //then
        Assertions.assertThat(actuals).isEmpty();
    }

    @DisplayName("param 값이 null 이면 emtpy 를 반환합니다.")
    @Test
    void test2() throws Exception {
        //given
        Class<?> clazz = HaveAnnotatedMethodClass.class;

        //when
        List<JavaMethodResolver> actuals = RequestMappingHttpMethodUrlMethodResolverExtractor.extract(clazz);

        //then
        Assertions.assertThat(actuals).isEmpty();
    }


    private static class DoesNotHaveAnnotatedMethodClass {

        public void doesNotAnnotatedMethod() {

        }

        public static Method getDoseNotAnnotatedMethod() {
            try {
                return DoesNotHaveAnnotatedMethodClass.class.getDeclaredMethod("doesNotAnnotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class HaveAnnotatedMethodClass {

        @RequestMapping(method = {HttpMethod.GET, HttpMethod.POST}, value = {"/test1", "/test2"})
        public void requestMappingAnnotatedMethod() {

        }

        public static Method getAnnotatedMethod() {
            try {
                return DoesNotHaveAnnotatedMethodClass.class.getDeclaredMethod("requestMappingAnnotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static List<HttpMethodUrlMatcher> getHttpMethodUrlMethodResolvers() {
            HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(HttpMethod.GET, "/test1");
            HttpMethodUrlMatcher httpMethodUrlMatcher1 = new HttpMethodUrlMatcher(HttpMethod.GET, "/test1");
            HttpMethodUrlMatcher httpMethodUrlMatcher2 = new HttpMethodUrlMatcher(HttpMethod.GET, "/test1");
            HttpMethodUrlMatcher httpMethodUrlMatcher3 = new HttpMethodUrlMatcher(HttpMethod.GET, "/test1");

            return List.of(httpMethodUrlMatcher, httpMethodUrlMatcher1, httpMethodUrlMatcher2, httpMethodUrlMatcher3);
        }
    }

}