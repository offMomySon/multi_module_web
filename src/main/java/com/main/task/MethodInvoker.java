package com.main.task;

import com.main.task.value.ParameterValue;
import container.ObjectRepository;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MethodInvoker {
    private static final Object EMPTY_VALUE = null;
    private final ObjectRepository objectRepository;

    public MethodInvoker(ObjectRepository objectRepository) {
        Objects.requireNonNull(objectRepository);
        this.objectRepository = objectRepository;
    }

    public Optional<Object> invoke(Method javaMethod, List<? extends ParameterValue<?>> parameterValues) {
        Objects.requireNonNull(javaMethod);
        Objects.requireNonNull(parameterValues);
        boolean hasNullParameterValue = parameterValues.stream().anyMatch(Objects::isNull);
        if (hasNullParameterValue) {
            throw new RuntimeException("Does not possible invoke. Any parameterValue is null.");
        }

        Class<?> declaringClass = javaMethod.getDeclaringClass();
        Object instance = objectRepository.get(declaringClass);
        Object[] values = parameterValues.stream()
            .map(ParameterValue::getValue)
            .map(v -> v.isPresent() ? v.get() : EMPTY_VALUE)
            .peek(v -> log.info("clazz : {}, value : {}", Objects.isNull(v) ? "empty" : v.getClass(), Objects.isNull(v) ? "null" : v))
            .toArray();

        Object result = doExecute(instance, javaMethod, values);
        return Optional.ofNullable(result);
    }

    private static Object doExecute(Object instance, Method javaMethod, Object[] paramsValues) {
        try {
            log.info("object : {}, javaMethod : {}, paramsValues : {}", instance, javaMethod, paramsValues);
            return javaMethod.invoke(instance, paramsValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
