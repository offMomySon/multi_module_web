package com.main.task.converter;

import com.main.task.value.ParameterValue;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PassParameterValueConverterTest {

    @DisplayName("빈값을 입력 받으면 빈값을 반환한다.")
    @Test
    void test() throws Exception {
        //given
        ParameterValue empty = ParameterValue.empty();
        PassParameterValueConverter converter = new PassParameterValueConverter(int.class);

        //when
        ParameterValue actual = converter.convert(empty);

        //then
        Assertions.assertThat(actual.isEmpty()).isTrue();
    }

    @DisplayName("target class 와 parameter value 의 class 가 다르면 excpeiton 이 발생한다.")
    @Test
    void ttest() throws Exception {
        //given
        ParameterValue empty = ParameterValue.from("String");
        PassParameterValueConverter converter = new PassParameterValueConverter(int.class);

        //when
        Throwable actual = Assertions.catchThrowable(()-> converter.convert(empty));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("입력받은 parameter value 를 그대로 반환한다.")
    @Test
    void tttest() throws Exception {
        //given
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[2]);
        ParameterValue parameterValue = ParameterValue.from(inputStream);
        PassParameterValueConverter passParameterValueConverter = new PassParameterValueConverter(InputStream.class);

        //when
        ParameterValue actual = passParameterValueConverter.convert(parameterValue);

        //then
        Assertions.assertThat(actual.getValue()).isEqualTo(parameterValue.getValue());
    }

}