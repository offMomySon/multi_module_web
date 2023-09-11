package com.main.task.converter.result;

import java.util.Optional;

public interface ResultConverter {
    Optional<?> convert(Optional<?> optionalResult);
}
