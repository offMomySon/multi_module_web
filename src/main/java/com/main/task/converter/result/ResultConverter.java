package com.main.task.converter.result;

import java.util.Optional;

public interface ResultConverter {
    Optional<Object> convert(Optional<Object> optionalResult);
}
