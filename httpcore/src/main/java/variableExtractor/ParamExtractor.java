package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface ParamExtractor {
    Optional<Object> extractValue(Parameter parameter);
}
