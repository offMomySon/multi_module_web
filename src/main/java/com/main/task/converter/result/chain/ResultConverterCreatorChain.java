package com.main.task.converter.result.chain;

import com.main.task.converter.result.ResultConverter;
import com.main.task.converter.result.creator.ResultConverterCreator;
import java.util.Objects;
import java.util.Optional;

public class ResultConverterCreatorChain {
    private final ResultConverterCreatorChain chain;
    private final ResultConverterCreator converterCreator;

    public ResultConverterCreatorChain(ResultConverterCreatorChain chain, ResultConverterCreator converterCreator) {
        Objects.requireNonNull(converterCreator);
        this.chain = chain;
        this.converterCreator = converterCreator;
    }

    public Optional<ResultConverter> create() {
        Optional<ResultConverter> optionalResultConverter = converterCreator.create();

        if (optionalResultConverter.isPresent()) {
            return optionalResultConverter;
        }

        if (Objects.isNull(chain)) {
            return Optional.empty();
        }

        return chain.create();
    }
}
