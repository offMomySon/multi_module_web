package com.main.task.converter.result.creator;

import com.main.task.converter.result.PassResultConverter;
import com.main.task.converter.result.ResultConverter;
import java.util.Optional;

public class PassResultConverterCreator implements ResultConverterCreator{
    @Override
    public Optional<ResultConverter> create() {
        ResultConverter converter = new PassResultConverter();
        return Optional.of(converter);
    }
}
