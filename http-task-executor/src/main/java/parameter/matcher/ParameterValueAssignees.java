package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import parameter.matcher.ParameterAndValueAssigneeType;
import parameter.matcher.ParameterValueAssignee;
import parameter.matcher.ParameterValueAssigneeType;

@Slf4j
public class ParameterValueAssignees {
    private final Map<ParameterValueAssigneeType, ParameterValueAssignee> valueAssignees;

    public ParameterValueAssignees(Map<ParameterValueAssigneeType, ParameterValueAssignee> valueAssignees) {
        Objects.requireNonNull(valueAssignees);
        this.valueAssignees = valueAssignees.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
        ;
    }

    public Optional<?> assign(ParameterAndValueAssigneeType parameterAndValueAssigneeType) {
        Objects.requireNonNull(parameterAndValueAssigneeType);

        ParameterValueAssigneeType parameterValueAssigneeType = parameterAndValueAssigneeType.getParameterValueAssigneeType();
        if (!valueAssignees.containsKey(parameterValueAssigneeType)) {
            throw new RuntimeException("Does not exist match type.");
        }
        ParameterValueAssignee parameterValueAssignee = valueAssignees.get(parameterValueAssigneeType);

        Parameter parameter = parameterAndValueAssigneeType.getParameter();
        Optional optionalAssignedValue = parameterValueAssignee.assign(parameter);
        log.info("optionalAssignedValue : `{}`", optionalAssignedValue);

        return optionalAssignedValue;
    }
}
