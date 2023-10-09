package com.main.task.executor;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import matcher.MatchedEndPointTaskWorker2;
import matcher.RequestMethod;
import matcher.segment.PathUrl;
import parameter.UrlParameterValues;
import parameter.matcher.ParameterValueAssignees2;
import task.EndPointTask2;
import task.worker.EndPointTaskWorker2;
import task.worker.EndPointWorkerResult;

@Slf4j
public class EndPointTaskExecutor {
    private final Function<UrlParameterValues, ParameterValueAssignees2> urlParameterValuesParameterValueAssignees2Function;
    private final EndPointTask2 endPointTask;

    public EndPointTaskExecutor(Function<UrlParameterValues, ParameterValueAssignees2> urlParameterValuesParameterValueAssignees2Function,
                                EndPointTask2 endPointTask) {
        Objects.requireNonNull(urlParameterValuesParameterValueAssignees2Function);
        Objects.requireNonNull(endPointTask);
        this.urlParameterValuesParameterValueAssignees2Function = urlParameterValuesParameterValueAssignees2Function;
        this.endPointTask = endPointTask;
    }


    public EndPointWorkerResult execute(RequestMethod method, PathUrl requestUrl) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(requestUrl);

        MatchedEndPointTaskWorker2 matchedEndPointTaskWorker = endPointTask.match(method, requestUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));

        UrlParameterValues pathVariableValue = new UrlParameterValues(matchedEndPointTaskWorker.getPathVariableValue().getValues());
        ParameterValueAssignees2 parameterValueAssignees = urlParameterValuesParameterValueAssignees2Function.apply(pathVariableValue);

        EndPointTaskWorker2 endPointTaskWorker = matchedEndPointTaskWorker.getEndPointTaskWorker();
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
