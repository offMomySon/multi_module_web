package com.main.invoker;

import com.main.extractor.ParameterValueExtractor;
import com.main.extractor.ParameterValueExtractorStrategy;
import container.ObjectRepository;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import matcher.converter.base.ObjectConverter;

@Slf4j
public class MethodInvoker {
    private static final ObjectConverter objectConverter = new ObjectConverter();

    private final ObjectRepository objectRepository;
    private final ParameterValueExtractorStrategy parameterValueExtractorStrategy;

    public MethodInvoker(ObjectRepository objectRepository, ParameterValueExtractorStrategy parameterValueExtractorStrategy) {
        Objects.requireNonNull(objectRepository);
        Objects.requireNonNull(parameterValueExtractorStrategy);
        this.objectRepository = objectRepository;
        this.parameterValueExtractorStrategy = parameterValueExtractorStrategy;
    }

    public Object invoke(Method javaMethod) {
        Objects.requireNonNull(javaMethod);

        Class<?> declaringClass = javaMethod.getDeclaringClass();
        Object instance = objectRepository.get(declaringClass);
        log.info("declaringClass : {}", declaringClass);
        log.info("instance : {}", instance);
        log.info("javaMethod : {}", javaMethod);

        Object[] values = Arrays.stream(javaMethod.getParameters())
            .map(parameterValueExtractorStrategy::create)
            .map(ParameterValueExtractor::extract)
            .map(extractValue -> {
                String value = extractValue.getOptionalValue().orElse("");
                Class<?> parameterType = extractValue.getParameterType();
                return objectConverter.convert(value, parameterType);
            })
            .peek(value -> log.info("value : {}, {}", value, value.getClass()))
            .toArray();

        return doInvoke(instance, javaMethod, values);
    }

    private static Object doInvoke(Object object, Method javaMethod, Object[] paramsValues) {
        try {
            log.info("object : {}, javaMethod : {}, paramsValues : {}", object.getClass(), javaMethod, paramsValues);
            return javaMethod.invoke(object, paramsValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
