package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Optional;

public class ChainParamValueExtractor implements ParamExtractor {
    private final ParamExtractor paramExtractor;
    private final ParamExtractor nextExtractor;

    public ChainParamValueExtractor(ParamExtractor paramExtractor, ParamExtractor nextExtractor) {
        this.paramExtractor = paramExtractor;
        this.nextExtractor = nextExtractor;
    }

    @Override
    public Optional<Object> extractValue(Parameter parameter) {
        Optional<Object> extract = paramExtractor.extractValue(parameter);
        if (extract.isPresent()) {
            return extract;
        }

        return nextExtractor.extractValue(parameter);
    }
}
