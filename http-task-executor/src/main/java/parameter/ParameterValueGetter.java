package parameter;

import converter.CompositeValueTypeConverter;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import parameter.matcher.MethodParameterValueMatcher;

@Slf4j
public class ParameterValueGetter {
    private static final CompositeValueTypeConverter converter = new CompositeValueTypeConverter();

    private final MethodParameterValueMatcher valueMatcher;

    public ParameterValueGetter(MethodParameterValueMatcher valueMatcher) {
        Objects.requireNonNull(valueMatcher);
        this.valueMatcher = valueMatcher;
    }

    public Optional<?> get(Parameter parameter) {
        Objects.requireNonNull(parameter);
        Class<?> parameterType = parameter.getType();
        log.info("parameter : `{}`, type : `{}`", parameter, parameterType);

        Optional optionalMatchValue = valueMatcher.match(parameter);
        if (optionalMatchValue.isEmpty()) {
            return Optional.empty();
        }

        String _matchedValue = (String) optionalMatchValue.get();
        Object value = converter.convertToClazz(_matchedValue, parameterType);
        log.info("ParameterValue. value : {}, class : {}", value, value.getClass());

        return Optional.of(value);
    }
}