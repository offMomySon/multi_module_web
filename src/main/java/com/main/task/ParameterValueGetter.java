package com.main.task;

import com.main.task.converter.ParameterValueConverter;
import com.main.task.converter.ParameterValueConverterFactory;
import com.main.task.value.MethodParameterValueMatcher;
import com.main.task.value.ParameterValue;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParameterValueGetter {
    private final MethodParameterValueMatcher valueMatcher;
    private final ParameterValueConverterFactory valueConverterFactory;

    public ParameterValueGetter(MethodParameterValueMatcher valueMatcher, ParameterValueConverterFactory valueConverterFactory) {
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
        Optional<?> value = valueConverter.convert(matchedValue);
        log.info("ParameterValue. value : {}, class : {}", value.orElse(null), value.map(Object::getClass).orElse(null));
        return value;
    }
}