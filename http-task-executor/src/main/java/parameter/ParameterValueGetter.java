package parameter;

import com.main.util.converter.CompositeValueTypeConverter;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import parameter.matcher.ParameterValueAssigneeType;
import parameter.matcher.ParameterAndValueAssigneeType;
import parameter.matcher.ParameterValueAssignees;

@Slf4j
public class ParameterValueGetter {
    private static final CompositeValueTypeConverter converter = new CompositeValueTypeConverter();

    private final ParameterValueAssignees parameterValueAssignee;

    public ParameterValueGetter(ParameterValueAssignees parameterValueAssignees) {
        Objects.requireNonNull(parameterValueAssignees);
        this.parameterValueAssignee = parameterValueAssignees;
    }

    public Optional<?> get(ParameterAndValueAssigneeType parameterAndValueAssigneeType) {
        Objects.requireNonNull(parameterAndValueAssigneeType);

        Parameter parameter = parameterAndValueAssigneeType.getParameter();
        ParameterValueAssigneeType parameterValueAssigneeType = parameterAndValueAssigneeType.getParameterValueAssigneeType();
        log.info("parameter : `{}`, parameterValueAssigneeType : `{}`", parameter, parameterValueAssigneeType);

        Optional optionalMatchValue = parameterValueAssignee.assign(parameterAndValueAssigneeType);
        if (optionalMatchValue.isEmpty()) {
            return Optional.empty();
        }

        Class<?> paramType = parameter.getType();
        String matchedValue = (String) optionalMatchValue.get();
        Object value = CompositeValueTypeConverter.convertToClazz(matchedValue, paramType);
        return Optional.of(value);
    }
}