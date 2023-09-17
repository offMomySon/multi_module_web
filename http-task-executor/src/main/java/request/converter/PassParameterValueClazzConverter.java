package request.converter;

import java.text.MessageFormat;
import java.util.Optional;

public class PassParameterValueClazzConverter implements ParameterValueConverter {
    private final Class<?> targetClazz;

    public PassParameterValueClazzConverter(Class<?> targetClazz) {
        this.targetClazz = targetClazz;
    }

    @Override
    public Optional<?> convertToParameterClazz(Optional<?> parameterValue) {
        if (parameterValue.isEmpty()) {
            return parameterValue;
        }

        Class<?> parameterValueClazz = parameterValue.get().getClass();
        boolean doesNotMatchClazz = !targetClazz.isAssignableFrom(parameterValueClazz);
        if (doesNotMatchClazz) {
            throw new RuntimeException(MessageFormat.format("Does not match clazz. parameterValueClazz : `{}`, targetClazz : `{}`", parameterValue, targetClazz));
        }

        return parameterValue;
    }
}
