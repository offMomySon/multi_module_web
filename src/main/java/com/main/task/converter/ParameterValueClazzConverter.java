package com.main.task.converter;

import java.util.Optional;

public interface ParameterValueClazzConverter {
    Optional<?> convert(Optional<?> parameterValue);
}
