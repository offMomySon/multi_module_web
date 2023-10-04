package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface ParameterValueMatcher<T> {
    Optional<T> match(Parameter parameter);
}
