package converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class ObjectValueConverter implements ValueConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<?> convertClazz;

    public ObjectValueConverter(Class<?> convertClazz) {
        Objects.requireNonNull(convertClazz);
        this.convertClazz = convertClazz;
    }

    @Override
    public InputStream convertToInputStream(Object object) {
        try {
            String jsonString = objectMapper.writeValueAsString(object);
            return new ByteArrayInputStream(jsonString.getBytes(UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object convertToClazz(String value) {
        Objects.requireNonNull(value);

        Object convertedValue = value;
        if(String.class != convertClazz){
            convertedValue = readValue(value, convertClazz);
        }

        log.info("convertedValue : `{}`, convertClazz : {}", convertedValue, convertClazz);
        return convertedValue;
    }

    private static Object readValue(String value, Class<?> convertClazz){
        try {
            return objectMapper.readValue(value, convertClazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
