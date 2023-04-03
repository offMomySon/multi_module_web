package variableExtractor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Stream;
import mapper.AnnotationUtils;
import marker.PathVariable;
import marker.RequestBody;
import marker.RequestParam;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vo.RequestBodyContent;
import vo.RequestParameters;

class ParameterConverterFactoryTest {

    @DisplayName("Parameter 가 null 이면 exception 이 발생합니다.")
    @Test
    void test1() throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> new ParameterConverterFactory(null, null, null));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("Parameter 가 가지고있는 annotation 에 매칭되는 converter 를 생성합니다.")
    @ParameterizedTest
    @MethodSource("provideMatchConverterAndParameter")
    void test0(Class<? extends ParameterConverter> parameterConverterClazz, Parameter parameter) throws Exception {
        //given
        ParameterConverterFactory parameterConverterFactory = new ParameterConverterFactory(
            RequestParameters.empty(),
            RequestParameters.empty(),
            RequestBodyContent.empty()
        );

        //when
        ParameterConverter actual = parameterConverterFactory.create(parameter);

        //then
        Assertions.assertThat(actual).isInstanceOf(parameterConverterClazz);
    }


    public static Stream<Arguments> provideMatchConverterAndParameter() {
        return Stream.of(
            Arguments.of(RequestParameterConverter.class, TestClass.getParameter(RequestParam.class)),
            Arguments.of(RequestParameterConverter.class, TestClass.getParameter(PathVariable.class)),
            Arguments.of(RequestBodyParameterConverter.class, TestClass.getParameter(RequestBody.class))
        );
    }


    private static class TestClass {
        public void annotatedMethod(@PathVariable String pathVariable,
                                    @RequestParam String requestParam,
                                    @RequestBody String requestBody) {
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

}