package com.main.executor;

import container.Container;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import variableExtractor.CompositeParameterConverter;

@Slf4j
public class MethodExecutor {
    private static final Objects EMPTY_VALUE = null;

    private final Container container;
    private final CompositeParameterConverter compositeParameterConverter;

    public MethodExecutor(Container container, CompositeParameterConverter compositeParameterConverter) {
        Objects.requireNonNull(container);
        Objects.requireNonNull(compositeParameterConverter);

        this.container = container;
        this.compositeParameterConverter = compositeParameterConverter;
    }

    public Object execute(Method javaMethod) {
        Objects.requireNonNull(javaMethod, "javaMethod is null.");

        Class<?> declaringClass = javaMethod.getDeclaringClass();

        Object instance = container.get(declaringClass);
        Object[] values = Arrays.stream(javaMethod.getParameters())
            .map(compositeParameterConverter::convertAsValue)
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
