package com.main.task.response;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FileHttpResponseHeaderCreatorTest {

    @DisplayName("파일의 확장자가 존재하지 않으면 Http response header 를 생성하지 못합니다.")
    @Test
    void test() throws Exception {
        //given
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Path path = Path.of("nonFileExtensionFile");
        FileHttpResponseHeaderCreator headerCreator = new FileHttpResponseHeaderCreator(dateFormat, "192.168..0.49", path);

        //when
        Throwable actual = Assertions.catchThrowable(headerCreator::create);

        //then
        Assertions.assertThat(actual).isNotNull();
    }


    @DisplayName("파일의 포멧에 맞는 Http response header 를 생성합니다.")
    @ParameterizedTest
    @CsvSource({
        "test.txt",
        "/base/test.txt",
        "test.gif",
        "/test.gif",
        "/base/test.gif",
        "test.jpg",
        "/base/test.jpg",
        "/base1/base2/test.jpg"})
    void ttest(String path) throws Exception {
        //given
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Path newPath = Path.of(path);
        FileHttpResponseHeaderCreator httpResponseCreator = new FileHttpResponseHeaderCreator(dateFormat, "192.168.0.49", newPath);

        //when
        Throwable actual = Assertions.catchThrowable(httpResponseCreator::create);

        //then
        Assertions.assertThat(actual).isNull();
    }
}