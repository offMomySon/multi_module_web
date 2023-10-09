package task.worker;

import java.util.Objects;
import lombok.Getter;

@Getter
public
class WorkerResult {
    private final WorkerResultType type;
    private final Object result;

    public WorkerResult(WorkerResultType type, Object result) {
        Objects.requireNonNull(type);
        this.type = type;
        this.result = result;
    }
}

