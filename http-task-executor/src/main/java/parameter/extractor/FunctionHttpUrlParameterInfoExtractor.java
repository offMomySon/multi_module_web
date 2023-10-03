package parameter.extractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.function.Function;

public class FunctionHttpUrlParameterInfoExtractor implements HttpUrlParameterInfoExtractor {
    private final Function<Parameter, HttpUrlParameterInfo> extractFunction;

    public FunctionHttpUrlParameterInfoExtractor(Function<Parameter, HttpUrlParameterInfo> extractFunction) {
        Objects.requireNonNull(extractFunction);
        this.extractFunction = extractFunction;
    }

    @Override
    public HttpUrlParameterInfo extract(Parameter parameter) {
        Objects.requireNonNull(parameter);
        return extractFunction.apply(parameter);
    }
}
