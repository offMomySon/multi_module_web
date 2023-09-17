package converter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;

public class EmptyConverter implements Converter<Object> {
    @Override
    public InputStream convertToInputStream(Object object) {
        return new ByteArrayInputStream("".getBytes(UTF_8));
    }


}