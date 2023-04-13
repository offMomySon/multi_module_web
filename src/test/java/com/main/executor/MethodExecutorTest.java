package com.main.executor;

import container.Container;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import marker.RequestParam;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import variableExtractor.RequestParameterConverter;
import vo.RequestValues;

class MethodExecutorTest {

    @DisplayName("method 를 가진 instance 가 존재하지 않으면 exception 이 발생합니다.")
    @Test
    void test() throws Exception {
        //given
        Container container = Container.empty();
        MethodExecutor methodExecutor = new MethodExecutor(container);
        Method method = TestClass.getMethod();

        //when
        Throwable actual = Assertions.catchThrowable(() -> methodExecutor.execute(method, new RequestParameterConverter(RequestParam.class, new RequestValues(Map.of("arg0", "failCase")))));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("java method 를 실행합니다.")
    @Test
    void test1() throws Exception {
        //given
        String mapValue = "length";
        Method method = TestClass.getMethod();
        Container container = new Container(Map.of(TestClass.class, new TestClass()));
        RequestValues requestValues = new RequestValues(Map.of("arg0", mapValue));
        MethodExecutor methodExecutor = new MethodExecutor(container);

        //when
        Optional<Object> actual = methodExecutor.execute(method, new RequestParameterConverter(RequestParam.class, requestValues));

        //then
        Assertions.assertThat(actual).isPresent();
        int length = (int) actual.get();
        Assertions.assertThat(length).isEqualTo(mapValue.length());
    }

    public static class TestClass {
        public TestClass() {
        }

        public int testMethod(@RequestParam String arg) {
            System.out.println("testMethod called. arg:" + arg);
            return arg.length();
        }

        public static Method getMethod() {
            try {
                return TestClass.class.getMethod("testMethod", String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

    }
}