package com.main.task.converter;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface ParameterValueConverter {
    Optional<Object> convert(Optional<Object> value);
}
