package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface ParameterValueAssignee<T> {
    Optional<T> assign(Parameter parameter);
}
