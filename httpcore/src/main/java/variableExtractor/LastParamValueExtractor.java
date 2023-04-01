package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Optional;

public class LastParamValueExtractor implements ParamExtractor {
    private final ParamExtractor paramExtractor;

    public LastParamValueExtractor(ParamExtractor paramExtractor) {
        this.paramExtractor = paramExtractor;
    }

    @Override
    public Optional<Object> extractValue(Parameter parameter) {
        Optional<Object> extract = paramExtractor.extractValue(parameter);
        return extract;
    }
}
