package parameter.matcher;

import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import parameter.matcher.MethodParameterValueMatcher;

public class BaseParameterValueMatcher<T> implements MethodParameterValueMatcher {
    private final T value;

    public BaseParameterValueMatcher(T value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    @Override
    public Optional<T> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Class<?> parameterClazz = parameter.getType();
        Class<?> valueClazz = value.getClass();

        boolean doesNotPossibleAssignValue = !parameterClazz.isAssignableFrom(valueClazz);
        if (doesNotPossibleAssignValue) {
            throw new RuntimeException(MessageFormat.format("Does not possible assign value. Parameter clazz : `{}`, Value clazz: `{}`", parameterClazz, valueClazz));
        }

        return Optional.of(value);
    }
}
