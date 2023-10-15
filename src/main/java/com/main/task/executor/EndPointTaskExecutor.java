package com.main.task.executor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import parameter.matcher.ParameterValueAssignees2;
import task.worker.EndPointTaskWorker2;
import task.worker.EndPointWorkerResult;

@Slf4j
public class EndPointTaskExecutor {
    private final ParameterValueAssignees2 parameterValueAssignees;

    public EndPointTaskExecutor(ParameterValueAssignees2 parameterValueAssignee) {
        Objects.requireNonNull(parameterValueAssignee);
        this.parameterValueAssignees = parameterValueAssignee;
    }

    public EndPointWorkerResult execute(EndPointTaskWorker2 endPointTaskWorker) {
        Objects.requireNonNull(endPointTaskWorker);
        log.info("ParameterTypeInfos: `{}`", Arrays.toString(endPointTaskWorker.getParameterTypeInfos()));

        Object[] parameterValues = Arrays.stream(endPointTaskWorker.getParameterTypeInfos())
            .map(parameterValueAssignees::assign)
            .map(v -> v.orElse(null))
            .toArray();

        EndPointWorkerResult endPointWorkerResult = endPointTaskWorker.execute(parameterValues);
        log.info("WorkerResultType: `{}`, result: `{}`, result clazz: `{}`",
                 endPointWorkerResult.getType(),
                 endPointWorkerResult.getResult(),
                 Optional.ofNullable(endPointWorkerResult.getResult()).map(Object::getClass).orElse(null));
        return endPointWorkerResult;
    }
}
