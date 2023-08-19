package com.main.task.policy;

import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

public class NoneAnnotatedParameterRequireChecker implements ParameterRequireChecker {
    private final Parameter parameter;
    private final Object value;

    public NoneAnnotatedParameterRequireChecker(Parameter parameter, Object value) {
        Objects.requireNonNull(parameter);
        Objects.requireNonNull(value);
        this.parameter = parameter;
        this.value = value;
    }

    @Override
    public void check(Optional<Object> parameterValue) {

    }
}
