package com.main.task.converter;

import java.text.MessageFormat;
import java.util.Optional;

public class PassParameterValueClazzClazzConverter implements ParameterValueClazzConverter {
    private final Class<?> targetClazz;

    public PassParameterValueClazzClazzConverter(Class<?> targetClazz) {
        this.targetClazz = targetClazz;
    }

    @Override
    public Optional<?> convert(Optional<?> parameterValue) {
        if (parameterValue.isEmpty()) {
            return parameterValue;
        }

        Class<?> parameterValueClazz = parameterValue.get().getClass();
        boolean doesNotMatchClazz = !targetClazz.isAssignableFrom(parameterValueClazz);
        if (doesNotMatchClazz) {
            throw new RuntimeException(MessageFormat.format("Does not match clazz. parameterValueClazz : `{}`, targetClazz : `{}`", parameterValue, targetClazz));
        }

        return parameterValue;
    }
}
