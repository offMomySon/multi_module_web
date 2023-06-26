package main.matcher.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestBody;
import matcher.annotation.RequestParam;
import matcher.converter.BodyContent;
import matcher.converter.RequestBodyParameterConverter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyParameterConverterTest {

    @DisplayName("변환 가능한 어노테인션을 가진 parameter 은 올바르게 동작합니다.")
    @Test
    void test1() throws Exception {
        //given
        RequestBodyParameterConverter converter = new RequestBodyParameterConverter(TestClass.getRequestBodyContent());
        Parameter parameter = TestClass.getRequestBodyAnnotatedParameter();
        TestDto expect = TestClass.getRequestBodyValue();

        //when
        Object actual = converter.convertAsValue(parameter).get();

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("변환 할 수 없는 어노테이션이을 가진 parameter 은 exception 이 발생합니다.")
    @Test
    void test2() throws Exception {
        //given
        Parameter[] parameters = TestClass.getPathVariableAndRequestParamAnnotatedParameters();
        RequestBodyParameterConverter converter = new RequestBodyParameterConverter(BodyContent.empty());

        //when
        List<Throwable> actuals = new ArrayList<>();
        for (Parameter parameter : parameters) {
            Throwable throwable = Assertions.catchThrowable(() -> converter.convertAsValue(parameter));
            actuals.add(throwable);
        }
        System.out.println(actuals);

        //then
        Assertions.assertThat(actuals).allSatisfy(actual -> Assertions.assertThat(actual).isNotNull());
    }

    @DisplayName("변환을 반드시 필요로하는 parameter 가 존재하고 body 의 내용이 비었으면 excpetion 이 발생합니다.")
    @Test
    void test3() throws Exception {
        //given
        RequestBodyParameterConverter converter = new RequestBodyParameterConverter(BodyContent.empty());
        Parameter parameter = TestClass.getRequestBodyAnnotatedParameter();

        //when
        Throwable actual = Assertions.catchThrowable(() -> converter.convertAsValue(parameter));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("변환을 조건적으로 필요로하는 parameter 가 존재하고 body 의 내용이 비었으면 빈값을 반환합니다.")
    @Test
    void test4() throws Exception {
        //given
        RequestBodyParameterConverter converter = new RequestBodyParameterConverter(BodyContent.empty());
        Parameter parameter = TestClass.getRequestBodyAnnotatedAndRequiredFalseParameter();

        //when
        Optional<Object> actual = converter.convertAsValue(parameter);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    private static class TestClass {
        public void pathVariableAndRequestParamAnnotatedMethod(@PathVariable(value = "pv1") String pv1, @PathVariable(value = "pv2") String pv2, @PathVariable(value = "pv3") String pv3,
                                                               @RequestParam(value = "pv4") String pv4, @RequestParam(value = "pv5") String pv5) {
        }

        public void requestBodyAnnotatedMethod(@RequestBody TestDto testDto) {
        }

        public void requestBodyAnnotatedAndRequiredFalseMethod(@RequestBody(required = false) TestDto testDto) {
        }

        public static Method getPathVariableAndRequestParamAnnotatedMethod() {
            try {
                return TestClass.class.getDeclaredMethod("pathVariableAndRequestParamAnnotatedMethod", String.class, String.class, String.class, String.class, String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Method getRequestBodyAnnotatedMethod() {
            try {
                return TestClass.class.getDeclaredMethod("requestBodyAnnotatedMethod", TestDto.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Method getRequestBodyAnnotatedAndRequiredFalseMethod() {
            try {
                return TestClass.class.getDeclaredMethod("requestBodyAnnotatedAndRequiredFalseMethod", TestDto.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static Parameter getRequestBodyAnnotatedParameter() {
            Method method = getRequestBodyAnnotatedMethod();
            return method.getParameters()[0];
        }

        public static Parameter getRequestBodyAnnotatedAndRequiredFalseParameter() {
            Method method = getRequestBodyAnnotatedAndRequiredFalseMethod();
            return method.getParameters()[0];
        }

        public static Parameter[] getPathVariableAndRequestParamAnnotatedParameters() {
            Method method = getPathVariableAndRequestParamAnnotatedMethod();
            return method.getParameters();
        }

        public static BodyContent getRequestBodyContent() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"tt\" : \"test\"");
            sb.append(", ");
            sb.append("\"vv\" : \"test\"}");
            String jsonBody = sb.toString();

            return new BodyContent(jsonBody);
        }


        public static TestDto getRequestBodyValue() {
            return new TestDto("test", "test");
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