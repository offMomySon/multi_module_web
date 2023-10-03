package parameter.extractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.function.Function;

public class FunctionBodyParameterInfoExtractor implements HttpBodyParameterInfoExtractor {
    private final Function<Parameter, HttpBodyParameterInfo> extractFunction;

    public FunctionBodyParameterInfoExtractor(Function<Parameter, HttpBodyParameterInfo> extractFunction) {
        Objects.requireNonNull(extractFunction);
        this.extractFunction = extractFunction;
    }

    @Override
    public HttpBodyParameterInfo extract(Parameter parameter) {
        Objects.requireNonNull(parameter);
        return extractFunction.apply(parameter);
    }
}
