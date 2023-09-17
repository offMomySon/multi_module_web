package request.converter;

import java.util.Optional;

public interface ParameterValueConverter {
    Optional<?> convertToParameterClazz(Optional<?> parameterValue);
}
