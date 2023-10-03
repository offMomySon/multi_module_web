package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.function.Function;

public class FunctionParameterTypeFinder implements ParameterTypeFinder{
    private final Function<Parameter, ParameterType> parameterParameterTypeFunction;

    public FunctionParameterTypeFinder(Function<Parameter, ParameterType> parameterParameterTypeFunction) {
        Objects.requireNonNull(parameterParameterTypeFunction);
        this.parameterParameterTypeFunction = parameterParameterTypeFunction;
    }

    @Override
    public ParameterType find(Parameter parameter) {
        Objects.requireNonNull(parameter);
        return parameterParameterTypeFunction.apply(parameter);
    }
}
