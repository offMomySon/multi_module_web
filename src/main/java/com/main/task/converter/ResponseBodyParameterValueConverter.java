package com.main.task.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.util.AnnotationUtils;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import matcher.annotation.RequestBody;

public class ResponseBodyParameterValueConverter implements ParameterValueConverter {
    private static final Class<RequestBody> REQUEST_BODY_CLASS = RequestBody.class;

    private final Class<?> parameterType;
    private final boolean required;
    private final ObjectMapper objectMapper;

    private ResponseBodyParameterValueConverter(Class<?> parameterType, boolean required, ObjectMapper objectMapper) {
        Objects.requireNonNull(parameterType);
        Objects.requireNonNull(objectMapper);
        this.parameterType = parameterType;
        this.required = required;
        this.objectMapper = objectMapper;
    }

    public static ResponseBodyParameterValueConverter from(Parameter parameter, ObjectMapper objectMapper) {
        Objects.requireNonNull(parameter);
        Objects.requireNonNull(objectMapper);

        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, REQUEST_BODY_CLASS);
        boolean doesNotExist = optionalRequestBody.isEmpty();
        if (doesNotExist) {
            throw new RuntimeException("Does not requestBody annotated parameter.");
        }

        Class<?> parameterType = parameter.getType();
        RequestBody requestBody = optionalRequestBody.get();
        boolean required = requestBody.required();
        return new ResponseBodyParameterValueConverter(parameterType, required, objectMapper);
    }

    @Override
    public Optional<Object> convert(Optional<Object> value) {
        if (value.isEmpty() && required) {
            throw new RuntimeException("does not exist value.");
        }

        if (value.isEmpty()) {
            return Optional.empty();
        }

        Object valueObject = value.get();
        if (!(valueObject instanceof String)) {
            throw new RuntimeException(MessageFormat.format("Does not String instance. valueObject : `{}`", valueObject.getClass()));
        }

        String content = (String) valueObject;
        Object newValue = readValue(content, parameterType);
        return Optional.of(newValue);
    }

    private Object readValue(String newValue, Class<?> parameterType) {
        try {
            return objectMapper.readValue(newValue, parameterType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
