package com.main.task.value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import annotation.RequestBody;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parameter.matcher.HttpBodyAnnotationAnnotatedParameterValueAssignee;

class HttpBodyAnnotationAnnotatedParameterValueAssigneeTest {

    @DisplayName("RequestBody annotation 이 존재하지 않으면 exception 이 발생합니다.")
    @Test
    void test() throws Exception {
        //given
        InputStream inputStream = new ByteArrayInputStream(new byte[1]);
        HttpBodyAnnotationAnnotatedParameterValueAssignee valueMatcher = new HttpBodyAnnotationAnnotatedParameterValueAssignee(inputStream);

        Parameter doesNotRequestBodyAnnotatedParameter = TestClass.getDoesNotRequestBodyAnnotatedParameter();

        //when
        Throwable actual = Assertions.catchThrowable(() -> valueMatcher.assign(doesNotRequestBodyAnnotatedParameter));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("match value 로 String 을 반환합니다.")
    @Test
    void ttest() throws Exception {
        //given
        InputStream inputStream = new ByteArrayInputStream(new byte[1]);
        HttpBodyAnnotationAnnotatedParameterValueAssignee valueMatcher = new HttpBodyAnnotationAnnotatedParameterValueAssignee(inputStream);

        Parameter requestBodyAnnotatedParameter = TestClass.getRequestBodyAnnotatedParameter();

        //when
        Optional<?> optionalActual = valueMatcher.assign(requestBodyAnnotatedParameter);

        //then
        Assertions.assertThat(optionalActual).isPresent();
        Object actual = optionalActual.get();
        boolean isStringClass = actual.getClass() == String.class;
        Assertions.assertThat(isStringClass).isTrue();
    }

    private static class TestClass {
        public static Parameter getDoesNotRequestBodyAnnotatedParameter() {
            Method testMethod = getTestMethod();
            Parameter[] parameters = testMethod.getParameters();
            return parameters[1];
        }

        public static Parameter getRequestBodyAnnotatedParameter() {
            Method testMethod = getTestMethod();
            Parameter[] parameters = testMethod.getParameters();
            return parameters[0];
        }

        private static Method getTestMethod() {
            try {
                return TestClass.class.getMethod("testMethod", int.class, int.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public void testMethod(@RequestBody int requestBodyAnnotated, int doesNotRequestBodyAnnotated) {

        }
    }
}