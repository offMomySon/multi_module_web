package com.main.task.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    public Optional<?> convert(Optional<?> parameterValue) {
        if (parameterValue.isEmpty()) {
            return parameterValue;
        }

        String value = (String) parameterValue.get();
        log.info("value : `{}`", value);

        if (targetClazz == String.class) {
            return Optional.of(value);
        }

        Object convertValue = readValue(targetClazz, value);
        log.info("convertValue : `{}`", convertValue);
        return Optional.of(convertValue);
    }

    private Object readValue(Class<?> targetClazz, String value) {
        try {
            return objectMapper.readValue(value, targetClazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
