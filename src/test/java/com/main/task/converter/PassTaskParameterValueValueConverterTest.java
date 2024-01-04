package com.main.task.converter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parameter.PassParameterValueClazzConverter;

class PassTaskParameterValueValueConverterTest {

    @DisplayName("빈값을 입력 받으면 빈값을 반환한다.")
    @Test
    void test() throws Exception {
        //given
        Optional<Object> empty = Optional.empty();
        PassParameterValueClazzConverter converter = new PassParameterValueClazzConverter(int.class);

        //when
        Optional<?> optionalActual = converter.convertToParameterTypeValue(empty);

        //then
        Assertions.assertThat(optionalActual).isEmpty();
    }

    @DisplayName("target class 와 parameter value 의 class 가 다르면 excpeiton 이 발생한다.")
    @Test
    void ttest() throws Exception {
        //given
        Optional<String> diffType = Optional.of("diffType");
        PassParameterValueClazzConverter converter = new PassParameterValueClazzConverter(int.class);

        //when
        Throwable actual = Assertions.catchThrowable(() -> converter.convertToParameterTypeValue(diffType));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("입력받은 parameter value 를 그대로 반환한다.")
    @Test
    void tttest() throws Exception {
        //given
        ByteArrayInputStream paramValue = new ByteArrayInputStream(new byte[2]);
        Optional<ByteArrayInputStream> parameterValue = Optional.of(paramValue);
        PassParameterValueClazzConverter passParameterValueClazzConverter = new PassParameterValueClazzConverter(InputStream.class);

        //when
        Optional<?> optionalActual = passParameterValueClazzConverter.convertToParameterTypeValue(parameterValue);

        //then
        Assertions.assertThat(optionalActual).isPresent();
        Object actual = optionalActual.get();
        Assertions.assertThat(actual).isEqualTo(paramValue);
    }

}