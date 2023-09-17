package converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
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
        if (String.class == targetClazz) {
            return (T) value;
        }

        try {
            log.info("value : {}", value);
            log.info("targetClazz : {}", targetClazz);
            return objectMapper.readValue(value, targetClazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
