package com.main.task.converter.parameter;

import java.util.Optional;

public interface ParameterValueConverter {
    Optional<?> convertToParameterClazz(Optional<?> parameterValue);
}
