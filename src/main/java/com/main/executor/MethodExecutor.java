package com.main.executor;

import container.Container;
import converter.ParameterConverter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MethodExecutor {
    private static final Objects EMPTY_VALUE = null;

    private final Container container;

    public MethodExecutor(Container container) {
        Objects.requireNonNull(container);

        this.container = container;
    }

    public Optional<Object> execute(Method javaMethod, ParameterConverter parameterConverter) {
        Objects.requireNonNull(javaMethod, "javaMethod is null.");
        Objects.requireNonNull(parameterConverter, "parameterConverter is null.");

        Class<?> declaringClass = javaMethod.getDeclaringClass();

        Object instance = container.get(declaringClass);
        log.info("instance : {}", instance);
        log.info("javaMethod : {}", javaMethod);

        Object[] values = Arrays.stream(javaMethod.getParameters())
            .peek(parameter -> log.info("parameter : `{}`, param class : `{}`", parameter, parameter.getClass()))
            .map(parameterConverter::convertAsValue)
            .peek(op -> log.info("parameterConverter : {}, {}", op.get(), op.get().getClass()))
            .map(optionalValue -> optionalValue.orElse(EMPTY_VALUE))
            .toArray();

        Object result = doExecute(instance, javaMethod, values);

        return Optional.ofNullable(result);
    }

    private static Object doExecute(Object object, Method javaMethod, Object[] paramsValues) {
        try {
            return javaMethod.invoke(object, paramsValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
