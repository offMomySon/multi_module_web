package com.main.task.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.task.value.ParameterValue;
import java.util.Objects;

public class BaseParameterValueConverter implements ParameterValueConverter {
    private final ObjectMapper objectMapper;
    private final Class<?> targetClazz;

    public BaseParameterValueConverter(ObjectMapper objectMapper, Class<?> targetClazz) {
        Objects.requireNonNull(objectMapper);
        Objects.requireNonNull(targetClazz);
        this.objectMapper = objectMapper;
        this.targetClazz = targetClazz;
    }

    @Override
    public ParameterValue<?> convert(ParameterValue<?> parameterValue) {
        if (parameterValue.isEmpty()) {
            return parameterValue;
        }

        String value = (String) parameterValue.getValue().get();

        if (targetClazz == String.class) {
            return ParameterValue.from(value);
        }

        Object convertValue = readValue(targetClazz, value);
        return ParameterValue.from(convertValue);
    }

    private Object readValue(Class<?> targetClazz, String value) {
        try {
            return objectMapper.readValue(value, targetClazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
