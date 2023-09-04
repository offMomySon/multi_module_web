package com.main.task.converter;

import java.util.Optional;

public interface ParameterValueConverter {
    Optional<?> convert(Optional<?> parameterValue);
}
