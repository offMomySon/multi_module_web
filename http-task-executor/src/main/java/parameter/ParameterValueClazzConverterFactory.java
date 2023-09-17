package parameter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.util.Objects;

public class ParameterValueClazzConverterFactory {
    private static final Class<InputStream> INPUT_STREAM_CLASS = InputStream.class;

    private final ObjectMapper objectMapper;

    public ParameterValueClazzConverterFactory(ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper);
        this.objectMapper = objectMapper;
    }

    public ParameterValueConverter create(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Class<?> parameterType = parameter.getType();
        boolean inputStreamAssignableParam = INPUT_STREAM_CLASS.isAssignableFrom(parameterType);
        if (inputStreamAssignableParam) {
            return new PassParameterValueClazzConverter(INPUT_STREAM_CLASS);
        }
        return new BaseParameterValueConverter(objectMapper, parameterType);
    }
}
