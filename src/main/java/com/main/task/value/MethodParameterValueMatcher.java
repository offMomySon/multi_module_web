package com.main.task.value;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface MethodParameterValueMatcher {
    ParameterValue<?> match(Parameter parameter);
}
