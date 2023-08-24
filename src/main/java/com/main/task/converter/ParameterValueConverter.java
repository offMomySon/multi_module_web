package com.main.task.converter;

import com.main.task.value.ParameterValue;

public interface ParameterValueConverter {
    ParameterValue<?> convert(ParameterValue<?> parameterValue);
}
