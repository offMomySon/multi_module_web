package com.main.task.response;

import java.net.InetAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FileHttpResponseHeaderCreatorTest {


    @DisplayName("")
    @Test
    void test() throws Exception {
        //given
        // Get the local host IP address
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println("Local Host IP Address: " + localhost.getHostAddress());

        // Get the IP address of a specific host by hostname
        String hostname = "www.example.com"; // Replace with the hostname you want to look up
        InetAddress address = InetAddress.getByName(hostname);
        System.out.println("IP Address of " + hostname + ": " + address.getHostAddress());


        //when

        //then

    }


    @DisplayName("변환가능한 자료형이면 Response 데이터를 생성합니다.")
    @ParameterizedTest
    @CsvSource({
        "test.txt",
        "/base/test.txt",
        "test.jpg",
        "/base/test.jpg",
        "/base1/base2/test.jpg"})
    void ttest(String path) throws Exception {
        //given
        SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));

        Path newPath = Path.of(path);
        FileHttpResponseHeaderCreator httpResponseCreator = new FileHttpResponseHeaderCreator(SIMPLE_DATE_FORMAT, "192.168.0.49", newPath);

        //when
        Throwable actual = Assertions.catchThrowable(httpResponseCreator::create);

        //then
        Assertions.assertThat(actual).isNull();
    }
}