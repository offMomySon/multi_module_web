package com.main.extractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;

public interface ParameterValueExtractor {
    ExtractValue extract();

    class ExtractValue {
        private final Class<?> parameterType;
        private final Optional<String> optionalValue;

        public ExtractValue(Class<?> parameterType, Optional<String> optionalValue) {
            Objects.requireNonNull(parameterType);
            Objects.requireNonNull(optionalValue);
            this.parameterType = parameterType;
            this.optionalValue = optionalValue;
        }

        public Class<?> getParameterType() {
            return parameterType;
        }

        public Optional<String> getOptionalValue() {
            return optionalValue;
        }
    }
}
