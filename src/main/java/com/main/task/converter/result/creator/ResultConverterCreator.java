package com.main.task.converter.result.creator;

import com.main.task.converter.result.ResultConverter;
import java.util.Optional;

public interface ResultConverterCreator {
    Optional<ResultConverter> create();
}
