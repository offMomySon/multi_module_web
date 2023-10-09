package task.worker;

import parameter.matcher.ParameterAndValueAssigneeType;

// object json
// object file
// object text

// 1. json string - object(none string)
// 2. file string - path
// 3. plain text - string

public interface EndPointTaskWorker2 {
    ParameterAndValueAssigneeType[] getParameterTypeInfos();

    EndPointWorkerResult execute(Object[] params);
}
