package variableExtractor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import marker.PathVariable;
import marker.RequestBody;
import marker.RequestParam;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import util.AnnotationUtils;
import vo.RequestParameters;

class RequestParameterConverterTest {

    @DisplayName("변환 가능한 어노테인션을 가진 parameter 은 올바르게 동작합니다.")
    @ParameterizedTest
    @MethodSource("provideAbleConvertAnnotatedParameters")
    void test1(Class<?> annotationType, Parameter parameter) throws Exception {
        //given
        RequestParameterConverter converter = new RequestParameterConverter(
            annotationType,
            new RequestParameters(Map.of("arg0", "1", "arg1", "2", "rp", "1")));

        //when
        Throwable actual = Assertions.catchThrowable(() -> converter.convertAsValue(parameter));

        //then
        Assertions.assertThat(actual).isNull();
    }

    @DisplayName("변환 할 수 없는 어노테이션이을 가진 parameter 은 exception 이 발생합니다.")
    @Test
    void test2() throws Exception {
        //given
        Parameter requestBodyAnnotatedParameter = TestClass.getParameter(RequestBody.class);

        //when
        Throwable actual = Assertions.catchThrowable(() -> new RequestParameterConverter(
            RequestBody.class,
            new RequestParameters(new HashMap<>())));

        //then
        Assertions.assertThat(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변환을 필요로하는 parameter 가 변환될 value 를 찾지못하면 excpetion 이 발생합니다.")
    @Test
    void test3() throws Exception {
        //given
        Parameter parameter = TestClass.getParameter(RequestParam.class);
        RequestParameterConverter converter = new RequestParameterConverter(
            RequestParam.class,
            TestClass.getDoesNotExistValueRequestParameters()
        );

        //when
        Throwable actual = Assertions.catchThrowable(() -> converter.convertAsValue(parameter));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("parameter 가 변환될 value 가 없으면 null 을 반환합니다.")
    @Test
    void test4() throws Exception {
        //given
        Parameter parameter = TestClassDoesNotRequired.getParameter(RequestParam.class);
        RequestParameterConverter converter = new RequestParameterConverter(
            RequestParam.class,
            TestClassDoesNotRequired.getDoesNotExistValueRequestParameters()
        );

        //when
        Optional<Object> actual = converter.convertAsValue(parameter);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @DisplayName("annotation 이 변환할 기준이되는 이름을 지정하지 않으면 parameter 이름을 기준으로 변환합니다.")
    @Test
    void test5() throws Exception {
        //given
        Parameter parameter = TestClass.getParameter(PathVariable.class);
        RequestParameters existValueRequestParameters = TestClass.getExistValueReuqestParameters();
        RequestParameterConverter converter = new RequestParameterConverter(
            PathVariable.class,
            existValueRequestParameters
        );

        String parameterName = parameter.getName();
        String expect = existValueRequestParameters.get(parameterName);

        //when
        Optional<Object> actual = converter.convertAsValue(parameter);

        //then
        Assertions.assertThat(actual).isPresent();
        Object actualValue = actual.get();
        Assertions.assertThat(actualValue).isEqualTo(expect);
    }

    public static Stream<Arguments> provideAbleConvertAnnotatedParameters() {

        return Stream.of(
            Arguments.of(RequestParam.class, TestClass.getParameter(RequestParam.class)),
            Arguments.of(PathVariable.class, TestClass.getParameter(PathVariable.class))
        );
    }


    private static class TestClass {
        public void annotatedMethod(@PathVariable String pathVariable,
                                    @RequestParam(value = "rp", required = true) String requestParam,
                                    @RequestBody String requestBody) {
        }

        public static RequestParameters getDoesNotExistValueRequestParameters() {
            Map<String, String> doesNotExistValue = Map.of("doesNotExistKey", "value");
            return new RequestParameters(doesNotExistValue);
        }

        public static RequestParameters getExistValueReuqestParameters() {
            Map<String, String> existValue = Map.of("arg0", "arg0",
                                                    "rp", "requestParam");
            return new RequestParameters(existValue);
        }

        private static Method getAnnotatedMethod() {
            try {
                return TestClass.class.getDeclaredMethod("annotatedMethod", String.class, String.class, String.class);
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

    private static class TestClassDoesNotRequired {
        public void annotatedMethod(@RequestParam(value = "rp", required = false) String requestParam) {

        }

        public static RequestParameters getDoesNotExistValueRequestParameters() {
            Map<String, String> doesNotExistValue = Map.of("doesNotExistKey", "value");
            return new RequestParameters(doesNotExistValue);
        }

        private static Method getAnnotatedMethod() {
            try {
                return TestClassDoesNotRequired.class.getDeclaredMethod("annotatedMethod", String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Parameter getParameter(Class<?> annotationClazz) {
            Parameter[] parameters = TestClassDoesNotRequired.getAnnotatedMethod().getParameters();

            return Arrays.stream(parameters)
                .filter(parameter -> AnnotationUtils.exist(parameter, annotationClazz))
                .findAny()
                .orElseThrow(() -> new RuntimeException("does not exist annotated param. find annotation : " + annotationClazz));
        }
    }


}