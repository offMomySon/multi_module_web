package converter.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ObjectConverter implements Converter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public InputStream convertToInputStream(Object object) {
        try {
            String jsonString = objectMapper.writeValueAsString(object);
            return new ByteArrayInputStream(jsonString.getBytes(UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T convert(String value, Class<T> targetClazz) {
        try {
            return objectMapper.readValue(value, targetClazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
