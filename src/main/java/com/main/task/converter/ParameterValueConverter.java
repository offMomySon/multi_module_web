package com.main.task.converter;

import com.main.task.value.ParameterValue;
import java.lang.reflect.Parameter;
import java.util.Optional;

public interface ParameterValueConverter {
    ParameterValue<?> convert(ParameterValue<?> parameterValue);
}
