package com.main.task.policy;

import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

public class NoneAnnotationParameterValuePolicy implements ParameterValuePolicy {
    private final Parameter parameter;
    private final Object value;

    public NoneAnnotationParameterValuePolicy(Parameter parameter, Object value) {
        Objects.requireNonNull(parameter);
        Objects.requireNonNull(value);
        this.parameter = parameter;
        this.value = value;
    }

    public Optional<Object> getValue() {
        Class<?> parameterType = parameter.getType();
        Class<?> objectClass = value.getClass();

        if (parameterType != objectClass) {
            throw new RuntimeException(
                MessageFormat.format("does not match parameter type. parameterType : `{}`, objectClass : `{}` ", parameterType, objectClass)
            );
        }
        return Optional.of(value);
    }
}
