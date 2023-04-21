package converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ObjectConverter implements Converter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public InputStream convertToInputStream(Object object) throws Exception {
        String jsonString = objectMapper.writeValueAsString(object);
        return new ByteArrayInputStream(jsonString.getBytes(UTF_8));
    }

}
