package com.main.task.converter;

import com.main.task.value.ParameterValue;
import java.text.MessageFormat;

public class PassParameterValueConverter implements ParameterValueConverter {
    private final Class<?> targetClazz;

    public PassParameterValueConverter(Class<?> targetClazz) {
        this.targetClazz = targetClazz;
    }

    @Override
    public ParameterValue<?> convert(ParameterValue<?> parameterValue) {
        if (parameterValue.isEmpty()) {
            return parameterValue;
        }

        Class<?> parameterValueClazz = parameterValue.getClazz();
        boolean doesNotMatchClazz = parameterValueClazz != targetClazz;
        if (doesNotMatchClazz) {
            throw new RuntimeException(MessageFormat.format("Does not match clazz. parameterValueClazz : `{}`, targetClazz : `{}`", parameterValue, targetClazz));
        }

        return parameterValue;
    }
}
