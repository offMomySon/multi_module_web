package parameter;

import java.util.Optional;

public interface ParameterValueConverter {
    Optional<?> convertToParameterTypeValue(Optional<?> parameterValue);
}
