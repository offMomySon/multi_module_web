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
        Object[] values = parameterValues.toArray();
        for (Object value : values) {
            log.info("value : {}, {}", value, value.getClass());
        }

        Object result = doExecute(instance, javaMethod, values);
        return Optional.ofNullable(result);
    }

    private static Object doExecute(Object object, Method javaMethod, Object[] paramsValues) {
        try {
            log.info("object : {}, javaMethod : {}, paramsValues : {}", object.getClass(), javaMethod, paramsValues);
            return javaMethod.invoke(object, paramsValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
