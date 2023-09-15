package task;

import java.lang.reflect.Parameter;
import java.util.Optional;

// object json
// object file
// object text

// 1. json string - object(none string)
// 2. file string - path
// 3. plain text - string

public interface EndPointTask {
    Parameter[] getExecuteParameters();
    Optional<Object> execute(Object[] params);
}
