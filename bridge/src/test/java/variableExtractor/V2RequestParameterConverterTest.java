package variableExtractor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;
import marker.PathVariable;
import marker.RequestParam;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vo.ParameterValues;

class V2RequestParameterConverterTest {
    private static final String REQUEST_PARAM_VALUE = "requestParamValue";
    private static final String REQUEST_PARAM_DEFAULT_VALUE = "requestParamDefaultValue";

    private static final String PATH_VARIABLE_VALUE = "pathVariableValue";

    @DisplayName("reuqestParam 어노테이션으로 부터 값을 추출합니다.")
    @Test
    void test() throws Exception {
        //given
        String testValue = "testValue";
        ParameterValues parameterValues = new ParameterValues(Map.of(
            REQUEST_PARAM_VALUE, testValue
        ));
        V2RequestParameterConverter converter = new V2RequestParameterConverter(parameterValues, RequestParam.class);

        //when
        Optional<Object> actual = converter.convertAsValue(TestClass.getRequestParam());

        //then
        Assertions.assertThat(actual).isPresent().get().isEqualTo(testValue);
    }

    @DisplayName("pathValirable 로 부터 값을 추출합니다.")
    @Test
    void test1() throws Exception {
        //given
        String testValue = "testValue";
        ParameterValues parameterValues = new ParameterValues(Map.of(
            PATH_VARIABLE_VALUE, testValue
        ));
        V2RequestParameterConverter converter = new V2RequestParameterConverter(parameterValues, PathVariable.class);

        //when
        Optional<Object> actual = converter.convertAsValue(TestClass.getPathVariable());

        //then
        Assertions.assertThat(actual).isPresent().get().isEqualTo(testValue);
    }

    public static class TestClass {
        public void testMethod(
            @RequestParam(value = REQUEST_PARAM_VALUE, defaultValue = REQUEST_PARAM_DEFAULT_VALUE) String requestParam,
            @PathVariable(value = PATH_VARIABLE_VALUE) String pathParam
        ) {
        }

        public static Method getMethod() {
            try {
                return TestClass.class.getMethod("testMethod", String.class, String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Parameter getRequestParam() {
            Method method = getMethod();
            return method.getParameters()[0];
        }

        public static Parameter getPathVariable() {
            Method method = getMethod();
            return method.getParameters()[1];
        }
    }

}