package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ParameterAndValueAssigneeType {
    private final Parameter parameter;
    private final ParameterValueAssigneType parameterValueAssigneType;

    public ParameterAndValueAssigneeType(Parameter parameter, ParameterValueAssigneType parameterValueAssigneType) {
        Objects.requireNonNull(parameter);
        Objects.requireNonNull(parameterValueAssigneType);
        this.parameter = parameter;
        this.parameterValueAssigneType = parameterValueAssigneType;
    }
}