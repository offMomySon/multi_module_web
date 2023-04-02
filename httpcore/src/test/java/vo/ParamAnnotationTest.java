package vo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import mapper.AnnotationUtils;
import marker.PathVariable;
import marker.RequestParam;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ParamAnnotationTest {

    @DisplayName("이름이 존재하지 않으면 exception 이 발생합니다.")
    @Test
    void test1() throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> new ParamAnnotation(null, true, "defaultValue"));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("defaultValue 가 존재하지 않아도 생성할 수 있습니다.")
    @Test
    void test2() throws Exception {
        //given
        //when
        ParamAnnotation actual = new ParamAnnotation("name", true, null);

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("RequestParam 어노테이션으로 생성할 수 있습니다.")
    @Test
    void test3() throws Exception {
        //given
        Parameter parameter = TestClass.getParameter(RequestParam.class);
        RequestParam requestParam = AnnotationUtils.find(parameter, RequestParam.class)
            .orElseThrow(() -> new RuntimeException("does not exist requestParam"));

        //when
        ParamAnnotation actual = ParamAnnotation.from(requestParam);

        //then
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getName()).isEqualTo("rp");
        Assertions.assertThat(actual.isRequired()).isEqualTo(false);

        Assertions.assertThat(actual.getDefaultValue()).isPresent();
        Assertions.assertThat(actual.getDefaultValue().get()).isEqualTo("defaultValue");
    }

    @DisplayName("PathVariable 어노테이션으로 생성할 수 있습니다.")
    @Test
    void test4() throws Exception {
        //given
        Parameter parameter = TestClass.getParameter(PathVariable.class);
        PathVariable pathVariable = AnnotationUtils.find(parameter, PathVariable.class)
            .orElseThrow(() -> new RuntimeException("does not exist requestParam"));

        //when
        ParamAnnotation actual = ParamAnnotation.from(pathVariable);

        //then
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getName()).isEqualTo("pv");
        Assertions.assertThat(actual.isRequired()).isEqualTo(false);

        Assertions.assertThat(actual.getDefaultValue()).isEmpty();
    }

    private static class TestClass {
        public void annotatedMethod(@RequestParam(value = "rp", required = false, defaultValue = "defaultValue") String requestParam,
                                    @PathVariable(value = "pv", required = false) String pathVariable) {
        }

        private static Method getAnnotatedMethod() {
            try {
                return TestClass.class.getDeclaredMethod("annotatedMethod", String.class, String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Parameter getParameter(Class<?> annotationClazz) {
            Parameter[] parameters = TestClass.getAnnotatedMethod().getParameters();

            return Arrays.stream(parameters)
                .filter(parameter -> AnnotationUtils.exist(parameter, annotationClazz))
                .findAny()
                .orElseThrow(() -> new RuntimeException("does not exist annotated param. find annotation : " + annotationClazz));
        }
    }

}