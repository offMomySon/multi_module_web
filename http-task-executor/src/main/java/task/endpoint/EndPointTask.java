package task.endpoint;

import java.util.Optional;
import parameter.matcher.ParameterAndValueMatcherType;

// object json
// object file
// object text

// 1. json string - object(none string)
// 2. file string - path
// 3. plain text - string

public interface EndPointTask {
    ParameterAndValueMatcherType[] getParameterTypeInfos();
    Optional<Object> execute(Object[] params);
}
