package com.main.task.value;

import java.lang.reflect.Parameter;

public interface MethodParameterValueMatcher {
    ParameterValue<?> match(Parameter parameter);
}
