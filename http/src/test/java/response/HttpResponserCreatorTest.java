package response;

import dto.ContentType;
import dto.ResponseStatus;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static java.nio.charset.StandardCharsets.UTF_8;

class HttpResponserCreatorTest {

    @DisplayName("입력한 content 에 대한 response 를 생성합니다.")
    @Test
    void test() {
        //given
        ResponseStatus responseStatus = ResponseStatus.OK;
        ContentType contentType = ContentType.TEXT;
        InputStream inputStream = new ByteArrayInputStream("teste".getBytes(UTF_8));

//        HttpResponseCreator httpResponseCreator = new HttpResponseCreator(status, contentType, createBufferedInputStream(inputStream));
//        byte[] send = httpResponseCreator.send();


    }
}