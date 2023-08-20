package com.main.task.value;

import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;

public class BaseParameterValueMatcher<T> implements MethodParameterValueMatcher {
    private final T value;

    public BaseParameterValueMatcher(T value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    @Override
    public ParameterValue<?> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Class<?> parameterClazz = parameter.getType();
        Class<?> valueClazz = value.getClass();

        boolean doesNotPossibleAssignValue = !parameterClazz.isAssignableFrom(valueClazz);
        if (doesNotPossibleAssignValue) {
            throw new RuntimeException(MessageFormat.format("Does not possible assign value. Parameter clazz : `{}`, Value clazz: `{}`", parameterClazz, valueClazz));
        }

        return ParameterValue.from(value);
    }
}
