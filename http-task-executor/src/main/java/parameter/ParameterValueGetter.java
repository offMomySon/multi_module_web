package parameter;

import converter.CompositeValueTypeConverter;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import parameter.matcher.ValueMatcherType;
import parameter.matcher.ParameterAndValueMatcherType;
import parameter.matcher.ParameterValueMatchers;

@Slf4j
public class ParameterValueGetter {
    private static final CompositeValueTypeConverter converter = new CompositeValueTypeConverter();

    private final ParameterValueMatchers valueMatchers;

    public ParameterValueGetter(ParameterValueMatchers valueMatchers) {
        Objects.requireNonNull(valueMatchers);
        this.valueMatchers = valueMatchers;
    }

    public Optional<?> get(ParameterAndValueMatcherType parameterAndValueMatcherType) {
        Objects.requireNonNull(parameterAndValueMatcherType);

        Parameter parameter = parameterAndValueMatcherType.getParameter();
        Class<?> paramType = parameter.getType();
        ValueMatcherType valueMatcherType = parameterAndValueMatcherType.getValueMatcherType();
        log.info("parameterAndValueMatcherType : `{}`, parameterType : `{}`", parameterAndValueMatcherType, valueMatcherType);

        Optional optionalMatchValue = valueMatchers.match(parameterAndValueMatcherType);
        if (optionalMatchValue.isEmpty()) {
            return Optional.empty();
        }

        String matchedValue = (String) optionalMatchValue.get();
        Object value = converter.convertToClazz(matchedValue, paramType);
        return Optional.of(value);
    }
}