package com.main.task.converter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.task.value.ParameterValue;
import java.io.ByteArrayInputStream;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BaseParameterValueConverterTest {

    @DisplayName("ParameterValue 는 string 이 아니면 exception 이 발생합니다.")
    @Test
    void test() throws Exception {
        //given
        ParameterValue parameterValue = ParameterValue.from(new ByteArrayInputStream(new byte[3]));
        BaseParameterValueConverter converter = new BaseParameterValueConverter(new ObjectMapper(), int.class);

        //when
        Throwable actual = Assertions.catchThrowable(() -> converter.convert(parameterValue));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("빈값을 전달하면 빈값을 반환합니다.")
    @Test
    void ttest() throws Exception {
        //given
        ParameterValue parameterValue = ParameterValue.empty();
        BaseParameterValueConverter converter = new BaseParameterValueConverter(new ObjectMapper(), int.class);

        //when
        ParameterValue<?> actual = converter.convert(parameterValue);

        //then
        Assertions.assertThat(actual.isEmpty()).isTrue();
    }

    @DisplayName("parameterValue 를 class 로 변환합니다.")
    @ParameterizedTest
    @MethodSource("provideConvertClassAndValue")
    void tttest(Class<?> clazz, String value) throws Exception {
        //given
        ParameterValue parameterValue = ParameterValue.from(value);
        BaseParameterValueConverter converter = new BaseParameterValueConverter(new ObjectMapper(), clazz);

        //when
        ParameterValue<?> actual = converter.convert(parameterValue);

        //then
        Assertions.assertThat(actual.isPresent()).isTrue();
        Assertions.assertThat(actual.getClazz()).isEqualTo(clazz);
    }

    private static Stream<Arguments> provideConvertClassAndValue() {
        return Stream.of(
            Arguments.of(String.class, "string"),
            Arguments.of(Integer.class, "1"),
            Arguments.of(Long.class, "2"),
            Arguments.of(int[].class, "[1,2,3,4]"),
            Arguments.of(String[].class, "[\"n\",\"a\",\"r\",\"r\",\"a\",\"y\"]"),
            Arguments.of(TestClass.class, "{\"intNum\":1,\"stringValue\":\"test\"}"),
            Arguments.of(Depth1TestClass.class, "{\"intNum\":1,\"sValue\":\"value\",\"testClass\":{\"intNum\":1,\"stringValue\":\"value\"}}"),
            Arguments.of(TestClass[].class, "[{\"intNum\":1,\"stringValue\":\"value1\"},{\"intNum\":2,\"stringValue\":\"value2\"},{\"intNum\":3,\"stringValue\":\"value3\"}]"),
            Arguments.of(Depth1TestClass[].class, "[{\"intNum\":1,\"sValue\":\"value\",\"testClass\":{\"intNum\":1,\"stringValue\":\"value\"}}," +
                "{\"intNum\":2,\"sValue\":\"value2\",\"testClass\":{\"intNum\":2,\"stringValue\":\"value2\"}} ]")
        );
    }

    private static class Depth1TestClass{
        private final int intNum;
        private final String sValue;
        private final TestClass testClass;

        @JsonCreator
        public Depth1TestClass(@JsonProperty("intNum") int intNum, @JsonProperty("sValue") String sValue, @JsonProperty("testClass") TestClass testClass) {
            this.intNum = intNum;
            this.sValue = sValue;
            this.testClass = testClass;
        }

        public int getIntNum() {
            return intNum;
        }

        public String getsValue() {
            return sValue;
        }

        public TestClass getTestClass() {
            return testClass;
        }
    }

    private static class TestClass{
        private final int intNum;
        private final String stringValue;

        @JsonCreator
        public TestClass(@JsonProperty("intNum") int intNum, @JsonProperty("stringValue") String stringValue) {
            this.intNum = intNum;
            this.stringValue = stringValue;
        }

        public int getIntNum() {
            return intNum;
        }

        public String getStringValue() {
            return stringValue;
        }
    }
}













