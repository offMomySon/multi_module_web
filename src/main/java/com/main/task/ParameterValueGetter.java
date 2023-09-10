package com.main.task;

import com.main.task.converter.parameter.ParameterValueConverter;
import com.main.task.converter.parameter.ParameterValueClazzConverterFactory;
import com.main.task.value.MethodParameterValueMatcher;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParameterValueGetter {
    private final MethodParameterValueMatcher valueMatcher;
    private final ParameterValueClazzConverterFactory valueConverterFactory;

    public ParameterValueGetter(MethodParameterValueMatcher valueMatcher, ParameterValueClazzConverterFactory valueConverterFactory) {
        Objects.requireNonNull(valueMatcher);
        Objects.requireNonNull(valueConverterFactory);
        this.valueMatcher = valueMatcher;
        this.valueConverterFactory = valueConverterFactory;
    }

    public Optional<?> get(Parameter parameter) {
        Objects.requireNonNull(parameter);

        log.info("parameter : `{}`, param class : `{}`", parameter, parameter.getType());
        Optional matchedValue = valueMatcher.match(parameter);

        ParameterValueConverter valueConverter = valueConverterFactory.create(parameter);
        Optional<?> value = valueConverter.convertToParameterClazz(matchedValue);
        log.info("ParameterValue. value : {}, class : {}", value.orElse(null), value.map(Object::getClass).orElse(null));
        return value;
    }
}