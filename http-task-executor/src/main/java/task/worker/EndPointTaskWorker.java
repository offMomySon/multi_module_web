package task.worker;

import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import parameter.matcher.ParameterAndValueAssigneeType;

// object json
// object file
// object text

// 1. json string - object(none string)
// 2. file string - path
// 3. plain text - string

public interface EndPointTaskWorker {
    ParameterAndValueAssigneeType[] getParameterTypeInfos();

    Optional<Object> execute(Object[] params);

    @Getter
    class WorkerResult {
        private final WorkerResultType type;
        private final Object result;

        public WorkerResult(WorkerResultType type, Object result) {
            Objects.requireNonNull(type);
            this.type = type;
            this.result = result;
        }
    }
}
