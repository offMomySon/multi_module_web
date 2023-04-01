package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface ParameterConverter {
    Optional<Object> convertValue(Parameter parameter);
}
