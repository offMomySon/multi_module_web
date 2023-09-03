package task;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface Task {
    Parameter[] getExecuteParameters();
    Optional<Object> execute(Object[] params);
}
