package converter;

import annotation.PathVariable;
import annotation.RequestBody;
import annotation.RequestParam;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import util.AnnotationUtils;
import vo.RequestParameters;

class RequestParametersConverterTest {

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

    @DisplayName("타깃어노테이션과 parameter 의 어노테이션이 다르면 empty value 를 반환합니다.")
    @Test
    void test2() throws Exception {
        //given
        Parameter requestBodyAnnotatedParameter = TestClass.getParameter(RequestParam.class);
        RequestParameterConverter converter = new RequestParameterConverter(
            PathVariable.class,
            new RequestParameters(new HashMap<>()));

        //when
        Optional<Object> actual = converter.convertAsValue(requestBodyAnnotatedParameter);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @DisplayName("변환을 필요로하는 parameter 가 변환될 value 를 찾지못하면 empty 이 발생합니다.")
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

    @DisplayName("모든 타입에 대한 값 변환이 가능합니다.")
    @Test
    void test() throws Exception {
        //given
        Parameter[] parameters = TestAllTypeParameterClass.getMethod().getParameters();
        RequestParameters requestParameters = TestAllTypeParameterClass.getRequestParameters();
        RequestParameterConverter converter = new RequestParameterConverter(RequestParam.class, requestParameters);

        List<Class<?>> expectTypes = Arrays.stream(parameters)
            .map(Parameter::getType)
            .filter(type -> !type.isPrimitive())
            .distinct()
            .collect(Collectors.toUnmodifiableList());

        //when
        List<Class<?>> actualTypes = Arrays.stream(parameters)
            .map(converter::convertAsValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Object::getClass)
            .distinct()
            .collect(Collectors.toUnmodifiableList());

        //then
        Assertions.assertThat(actualTypes).containsExactlyInAnyOrderElementsOf(expectTypes);
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

    private static class TestAllTypeParameterClass {
        public void annotatedMethod(
            @RequestParam(value = "boolean") boolean argboolean,
            @RequestParam(value = "Boolean") Boolean argBoolean,
            @RequestParam(value = "int") int argint,
            @RequestParam(value = "Integer") Integer argInteger,
            @RequestParam(value = "long") long arglong,
            @RequestParam(value = "Long") Long argLong,
            @RequestParam(value = "float") float argfloat,
            @RequestParam(value = "Float") Float argFloat,
            @RequestParam(value = "double") double argdouble,
            @RequestParam(value = "Double") Double argDouble,
            @RequestParam(value = "String") String argString
        ) {
        }


        private static RequestParameters getRequestParam() {
            return new RequestParameters(Map.of());
        }

        private static Method getMethod() {
            try {
                return TestAllTypeParameterClass.class.getDeclaredMethod("annotatedMethod",
                                                                         boolean.class, Boolean.class,
                                                                         int.class, Integer.class,
                                                                         long.class, Long.class,
                                                                         float.class, Float.class,
                                                                         double.class, Double.class,
                                                                         String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        private static RequestParameters getRequestParameters() {
            Map<String, String> value = new HashMap<>();
            value.put("boolean", "false");
            value.put("Boolean", "false");
            value.put("int", "33");
            value.put("Integer", "333");
            value.put("long", "44");
            value.put("Long", "444");
            value.put("float", "3.14");
            value.put("Float", "3.14");
            value.put("double", "3.111111");
            value.put("Double", "3.141241");
            value.put("String", "string");
            return new RequestParameters(value);
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