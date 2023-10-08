package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ParameterAndValueAssigneeType {
    private final Parameter parameter;
    private final ParameterValueAssigneeType parameterValueAssigneeType;

    public ParameterAndValueAssigneeType(Parameter parameter, ParameterValueAssigneeType parameterValueAssigneeType) {
        Objects.requireNonNull(parameter);
        Objects.requireNonNull(parameterValueAssigneeType);
        this.parameter = parameter;
        this.parameterValueAssigneeType = parameterValueAssigneeType;
    }
}