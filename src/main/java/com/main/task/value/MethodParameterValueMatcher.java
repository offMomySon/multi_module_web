package com.main.task.value;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface MethodParameterValueMatcher<T> {
    Optional<T> match(Parameter parameter);
}
