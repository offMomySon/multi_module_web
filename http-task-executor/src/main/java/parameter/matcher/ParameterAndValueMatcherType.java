package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ParameterAndValueMatcherType {
    private final Parameter parameter;
    private final ValueMatcherType valueMatcherType;

    public ParameterAndValueMatcherType(Parameter parameter, ValueMatcherType valueMatcherType) {
        Objects.requireNonNull(parameter);
        Objects.requireNonNull(valueMatcherType);
        this.parameter = parameter;
        this.valueMatcherType = valueMatcherType;
    }
}