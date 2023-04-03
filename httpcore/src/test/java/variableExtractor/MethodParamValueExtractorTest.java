package variableExtractor;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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
import vo.RequestBodyContent;
import vo.RequestParameters;

class MethodParamValueExtractorTest {

    @DisplayName("constructor param 이 null 이면 exepction 이 발생합니다.")
    @Test
    void test1() throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> new MethodParamValueExtractor(null, null));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("method 의 parameters 을 values 로 변환합니다.")
    @ParameterizedTest
    @MethodSource("provideMethodAndExpectValues")
    void test2(Method method, Object[] expects) throws Exception {
        //given
        ParameterConverterFactory parameterConverterFactory = new ParameterConverterFactory(
            TestClass.getRequestParamRequestParameters(),
            TestClass.getPathVariableRequestParameters(),
            TestClass.getRequestBodyContent()
        );
        MethodParamValueExtractor methodParamValueExtractor = new MethodParamValueExtractor(parameterConverterFactory, method);

        //when
        Object[] actuals = methodParamValueExtractor.extractValues();

        //then
        Assertions.assertThat(actuals)
            .containsAll(Arrays.stream(expects).collect(Collectors.toUnmodifiableList()));
    }

    public static Stream<Arguments> provideMethodAndExpectValues() {
        return Stream.of(
            Arguments.of(TestClass.getPathVariableAndRequestParamAnnotatedMethod(),
                         TestClass.getPathVariableAndRequestParamValues()),
            Arguments.of(TestClass.getRequestBodyAnnotatedMethod(), TestClass.getRequestBodyValues())
        );
    }

    private static class TestClass {
        public void pathVariableAndRequestParamAnnotatedMethod(
            @PathVariable(value = "pv1") String pv1,
            @PathVariable(value = "pv2") String pv2,
            @PathVariable(value = "pv3") String pv3,
            @RequestParam(value = "pv4") String pv4,
            @RequestParam(value = "pv5") String pv5) {
        }

        public void requestBodyAnnotatedMethod(@RequestBody TestDto testDto) {
        }

        public static RequestParameters getRequestParamRequestParameters() {
            return new RequestParameters(Map.of("pv4", "pv4",
                                                "pv5", "pv5"));
        }

        public static RequestParameters getPathVariableRequestParameters() {
            return new RequestParameters(Map.of("pv1", "pv1",
                                                "pv2", "pv2",
                                                "pv3", "pv3"));
        }

        public static Object[] getPathVariableAndRequestParamValues() {
            return new Object[]{"pv1", "pv2", "pv3", "pv4", "pv5"};
        }

        public static Object[] getRequestBodyValues() {
            return new Object[]{new TestDto("test", "test")};
        }

        public static RequestBodyContent getRequestBodyContent() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"tt\" : \"test\"");
            sb.append(", ");
            sb.append("\"vv\" : \"test\"}");
            String jsonBody = sb.toString();

            return new RequestBodyContent(jsonBody);
        }

        public static Method getPathVariableAndRequestParamAnnotatedMethod() {
            try {
                return TestClass.class.getDeclaredMethod("pathVariableAndRequestParamAnnotatedMethod",
                                                         String.class, String.class, String.class, String.class, String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Method getRequestBodyAnnotatedMethod() {
            try {
                return TestClass.class.getDeclaredMethod("requestBodyAnnotatedMethod",
                                                         TestDto.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class TestDto {
        @JsonProperty("tt")
        private String tt;
        @JsonProperty("vv")
        private String vv;

        public TestDto() {
        }

        public TestDto(String tt, String vv) {
            this.tt = tt;
            this.vv = vv;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestDto testDto = (TestDto) o;
            return Objects.equals(tt, testDto.tt) && Objects.equals(vv, testDto.vv);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tt, vv);
        }
    }
}