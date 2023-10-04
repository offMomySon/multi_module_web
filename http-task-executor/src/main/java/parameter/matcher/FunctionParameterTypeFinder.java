package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.function.Function;

public class FunctionParameterTypeFinder implements ParameterTypeFinder{
    private final Function<Parameter, ValueMatcherType> parameterParameterTypeFunction;

    public FunctionParameterTypeFinder(Function<Parameter, ValueMatcherType> parameterParameterTypeFunction) {
        Objects.requireNonNull(parameterParameterTypeFunction);
        this.parameterParameterTypeFunction = parameterParameterTypeFunction;
    }

    @Override
    public ValueMatcherType find(Parameter parameter) {
        Objects.requireNonNull(parameter);
        return parameterParameterTypeFunction.apply(parameter);
    }
}
