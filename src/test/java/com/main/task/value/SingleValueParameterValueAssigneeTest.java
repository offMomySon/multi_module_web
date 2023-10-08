package com.main.task.value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parameter.matcher.SingleValueParameterValueAssignee;

class SingleValueParameterValueAssigneeTest {

    @DisplayName("parameter class 에 value 를 할당할 수 있으면 값을 반환합니다.")
    @Test
    void test() throws Exception {
        //given
        Parameter inputStreamParameter = TestClass.getInputStreamParameter();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[1]);
        SingleValueParameterValueAssignee<InputStream> baseParameterValueMatcher = new SingleValueParameterValueAssignee<>(byteArrayInputStream);

        //when
        Throwable actual = Assertions.catchThrowable(() -> baseParameterValueMatcher.assign(inputStreamParameter));

        //then
        Assertions.assertThat(actual).isNull();
    }

    @DisplayName("parameter class 에 value 를 할당할 수 없으면 exception 이 발생합니다.")
    @Test
    void ttest() throws Exception {
        //given
        Parameter inputStreamParameter = TestClass.getInputStreamParameter();
        SingleValueParameterValueAssignee<Integer> baseParameterValueMatcher = new SingleValueParameterValueAssignee<>(1);

        //when
        Throwable actual = Assertions.catchThrowable(() -> baseParameterValueMatcher.assign(inputStreamParameter));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    private static class TestClass {

        public static Parameter getInputStreamParameter() {
            Method testMethod = getTestMethod();
            Parameter[] parameters = testMethod.getParameters();
            return parameters[0];
        }

        private static Method getTestMethod() {
            try {
                return TestClass.class.getMethod("testMethod", InputStream.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public void testMethod(InputStream inputStream) {

        }
    }

}