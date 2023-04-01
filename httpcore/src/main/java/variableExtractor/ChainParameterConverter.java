package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Optional;

public class ChainParameterConverter implements ParameterConverter {
    private final ParameterConverter parameterConverter;
    private final ParameterConverter nextExtractor;

    public ChainParameterConverter(ParameterConverter parameterConverter, ParameterConverter nextExtractor) {
        this.parameterConverter = parameterConverter;
        this.nextExtractor = nextExtractor;
    }

    @Override
    public Optional<Object> convertValue(Parameter parameter) {
        Optional<Object> extract = parameterConverter.convertValue(parameter);
        if (extract.isPresent()) {
            return extract;
        }

        return nextExtractor.convertValue(parameter);
    }
}
