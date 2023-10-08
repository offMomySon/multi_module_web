package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.function.Function;

public class FunctionParameterTypeFinder implements ParameterTypeFinder{
    private final Function<Parameter, ParameterValueAssigneType> parameterParameterTypeFunction;

    public FunctionParameterTypeFinder(Function<Parameter, ParameterValueAssigneType> parameterParameterTypeFunction) {
        Objects.requireNonNull(parameterParameterTypeFunction);
        this.parameterParameterTypeFunction = parameterParameterTypeFunction;
    }

    @Override
    public ParameterValueAssigneType find(Parameter parameter) {
        Objects.requireNonNull(parameter);
        return parameterParameterTypeFunction.apply(parameter);
    }
}
