package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Optional;

public class LastParameterConverter implements ParameterConverter {
    private final ParameterConverter parameterConverter;

    public LastParameterConverter(ParameterConverter parameterConverter) {
        this.parameterConverter = parameterConverter;
    }

    @Override
    public Optional<Object> convertAsValue(Parameter parameter) {
        Optional<Object> extract = parameterConverter.convertAsValue(parameter);
        return extract;
    }
}
