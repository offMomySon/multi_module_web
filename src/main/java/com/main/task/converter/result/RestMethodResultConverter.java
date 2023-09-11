package com.main.task.converter.result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;

public class RestMethodResultConverter implements ResultConverter {

    private final ObjectMapper objectMapper;

    public RestMethodResultConverter(ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<?> convert(Optional<?> optionalResult) {
        if (optionalResult.isEmpty()) {
            return optionalResult;
        }

        Object result = optionalResult.get();
        String jsonResult = writeValueAsString(result);
        return Optional.of(jsonResult);
    }

    private String writeValueAsString(Object result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
