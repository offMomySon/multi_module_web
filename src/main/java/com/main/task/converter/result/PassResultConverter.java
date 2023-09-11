package com.main.task.converter.result;

import java.util.Objects;
import java.util.Optional;

public class PassResultConverter implements ResultConverter {
    @Override
    public Optional<?> convert(Optional<?> optionalResult) {
        Objects.requireNonNull(optionalResult);
        return optionalResult;
    }
}
