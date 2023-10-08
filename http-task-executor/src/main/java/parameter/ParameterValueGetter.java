package parameter;

import converter.CompositeValueTypeConverter;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import parameter.matcher.ParameterValueAssigneType;
import parameter.matcher.ParameterAndValueAssigneeType;
import parameter.matcher.ParameterValueMatchers;

@Slf4j
public class ParameterValueGetter {
    private static final CompositeValueTypeConverter converter = new CompositeValueTypeConverter();

    private final ParameterValueMatchers valueMatchers;

    public ParameterValueGetter(ParameterValueMatchers valueMatchers) {
        Objects.requireNonNull(valueMatchers);
        this.valueMatchers = valueMatchers;
    }

    public Optional<?> get(ParameterAndValueAssigneeType parameterAndValueAssigneeType) {
        Objects.requireNonNull(parameterAndValueAssigneeType);

        Parameter parameter = parameterAndValueAssigneeType.getParameter();
        ParameterValueAssigneType parameterValueAssigneType = parameterAndValueAssigneeType.getParameterValueAssigneType();
        log.info("parameter : `{}`, valueMatcherType : `{}`", parameter, parameterValueAssigneType);

        Optional optionalMatchValue = valueMatchers.match(parameterAndValueAssigneeType);
        if (optionalMatchValue.isEmpty()) {
            return Optional.empty();
        }

        Class<?> paramType = parameter.getType();
        String matchedValue = (String) optionalMatchValue.get();
        Object value = converter.convertToClazz(matchedValue, paramType);
        return Optional.of(value);
    }
}