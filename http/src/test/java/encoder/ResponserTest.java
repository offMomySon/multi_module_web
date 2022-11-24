package encoder;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import response.generator.Responser;

class ResponserTest {

    @Test
    void teste(){

        String text = "test text";

        Responser.builder()
            .status(Responser.Status.OK)
            .contentType(Responser.ContentType.TEXT)
            .contentInputStream(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));

    }

}