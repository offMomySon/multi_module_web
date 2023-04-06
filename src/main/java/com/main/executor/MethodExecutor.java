package com.main.executor;

import container.Container;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import variableExtractor.ParameterConverter;

public class MethodExecutor {
    private static final Objects EMPTY_VALUE = null;

    private final Container container;
    private final ParameterConverter parameterConverter;

    public MethodExecutor(Container container, ParameterConverter parameterConverter) {
        this.container = container;
        this.parameterConverter = parameterConverter;
    }

    public Object execute(Method javaMethod) {
        Objects.requireNonNull(javaMethod, "javaMethod is null.");

        Class<?> declaringClass = javaMethod.getDeclaringClass();

        Object instance = container.get(declaringClass);
        Object[] values = Arrays.stream(javaMethod.getParameters())
            .map(parameterConverter::convertAsValue)
            .map(optionalObject -> optionalObject.orElse(EMPTY_VALUE))
            .toArray();

        return doExecute(instance, javaMethod, values);
    }

    private static Object doExecute(Object object, Method javaMethod, Object[] paramsValues) {
        try {
            return javaMethod.invoke(object, paramsValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
