package com.main.task.value;

import java.util.Optional;

public class ParameterValue<T> {
    private final Optional<T> value;

    public ParameterValue(Optional<T> value) {
        this.value = value;
    }

    public static <T> ParameterValue from(T value) {
        return new ParameterValue(Optional.ofNullable(value));
    }

    public static ParameterValue empty() {
        return new ParameterValue(Optional.empty());
    }

    public boolean isPresent() {
        return value.isPresent();
    }

    public boolean isEmpty() {
        return !isPresent();
    }

    public Optional<T> getValue() {
        return value;
    }

    public boolean isAssignableFrom(Class<?> clazz) {
        if (isEmpty()) {
            return false;
        }

        T t = value.get();
        return clazz.isAssignableFrom(t.getClass());
    }

    public Class<?> getClazz() {
        if (isEmpty()) {
            throw new RuntimeException("value is empty.");
        }

        T t = value.get();
        return t.getClass();
    }
}
