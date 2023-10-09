package task.worker;

import java.util.Objects;
import lombok.Getter;

@Getter
public
class EndPointWorkerResult {
    private final WorkerResultType type;
    private final Object result;

    public EndPointWorkerResult(WorkerResultType type, Object result) {
        Objects.requireNonNull(type);
        this.type = type;
        this.result = result;
    }
}

