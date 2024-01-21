package com.main.method_invoker;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;

public class ParameterValueRepository {
    private final Map<String, ParameterValue> values;

    public ParameterValueRepository(@NonNull Map<String, ParameterValue> values) {
        this.values = Map.copyOf(values);
    }

    public Optional<ParameterValue> findValueByParameterName(@NonNull ParameterName parameterName) {
        Optional<ParameterValue> optionalValue = doFindValueByParameterName(parameterName.getCustomName());
        if (optionalValue.isPresent()) {
            return optionalValue;
        }

        return doFindValueByParameterName(parameterName.getOriginName());
    }

    private Optional<ParameterValue> doFindValueByParameterName(String paramName) {
        if (Objects.isNull(paramName) || paramName.isBlank()) {
            return Optional.empty();
        }

        if (!values.containsKey(paramName)) {
            return Optional.empty();
        }

        ParameterValue parameterValue = values.get(paramName);
        return Optional.of(parameterValue);
    }

    @Getter
    public static class ParameterName {
        private final String customName;
        private final String originName;

        public ParameterName(String customName, @NonNull String originName) {
            this.customName = customName;
            this.originName = originName;
        }
    }

    @Getter
    public static class ParameterValue {
        private final Object value;

        public ParameterValue(@NonNull Object value) {
            if (!value.getClass().isAssignableFrom(String.class) && !value.getClass().isAssignableFrom(InputStream.class)) {
                throw new RuntimeException("Invalid parameter. Must be value is assignable from String or Inputstream");
            }
            this.value = value;
        }

        public Class<?> getType() {
            return value.getClass();
        }
    }
}