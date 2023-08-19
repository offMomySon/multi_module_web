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

    public static <T> ParameterValue empty() {
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
}
