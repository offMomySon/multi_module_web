package com.main.task.executor;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import matcher.MatchedEndPointTaskWorker2;
import matcher.RequestMethod;
import matcher.segment.PathUrl;
import parameter.UrlParameterValues;
import parameter.matcher.ParameterValueAssignee;
import parameter.matcher.ParameterValueAssignees2;
import task.CompositedEndpointTasks;
import task.EndPointTask2;
import task.worker.EndPointTaskWorker2;
import task.worker.WorkerResult;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.QueryParameters;
import static parameter.matcher.ParameterValueAssigneeType.BODY;
import static parameter.matcher.ParameterValueAssigneeType.QUERY_PARAM;
import static parameter.matcher.ParameterValueAssigneeType.URL;

@Slf4j
public class EndPointTaskExecutor {
    private final Function<UrlParameterValues, ParameterValueAssignees2> urlParameterValuesParameterValueAssignees2Function;
    private final EndPointTask2 endPointTask;
    private final SimpleDateFormat simpleDateFormat;
    private final String hostAddress;

    public EndPointTaskExecutor(Function<UrlParameterValues, ParameterValueAssignees2> urlParameterValuesParameterValueAssignees2Function,
                                EndPointTask2 endPointTask,
                                SimpleDateFormat simpleDateFormat,
                                String hostAddress) {
        Objects.requireNonNull(urlParameterValuesParameterValueAssignees2Function);
        Objects.requireNonNull(endPointTask);
        Objects.requireNonNull(simpleDateFormat);
        Objects.requireNonNull(hostAddress);
        this.urlParameterValuesParameterValueAssignees2Function = urlParameterValuesParameterValueAssignees2Function;
        this.endPointTask = endPointTask;
        this.simpleDateFormat = simpleDateFormat;
        this.hostAddress = hostAddress;
    }


    public WorkerResult execute(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        RequestMethod method = RequestMethod.find(request.getHttpMethod().name());
        PathUrl requestUrl = PathUrl.from(request.getHttpRequestPath().getValue().toString());

        MatchedEndPointTaskWorker2 matchedEndPointTaskWorker = endPointTask.match(method, requestUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));
        EndPointTaskWorker2 endPointTaskWorker = matchedEndPointTaskWorker.getEndPointTaskWorker();

        UrlParameterValues pathVariableValue = new UrlParameterValues(matchedEndPointTaskWorker.getPathVariableValue().getValues());

        // todo [review]
        // 받은 피드백 - annotation 모듈을 이용해서 동적으로 처리해라
        // 나의 구현 - 피드백대로 수행하지 않았다.
        // parameter 에 어노테이팅된 정보들을 여기서 해석하지 않고 생성시 미리 해석하는 방식으로 진행한다.
        // 해석된 정보를 이용하여 로직을 수행하기 때문에 annotation 과 연관관계를 끊을 수 있다.
        // 하지만 코드 적으로는 끊어졌지만, 개념적으로는 연결이 되어있다. 이것을 연관관계를 끊어다고 볼 수 있을까?
        // 임시저장 브랜치 - origin/split_annotation_module_role_at_MethodParameterValueMatcher
        ParameterValueAssignees2 parameterValueAssignees = urlParameterValuesParameterValueAssignees2Function.apply(pathVariableValue);

        Object[] parameterValues = Arrays.stream(endPointTaskWorker.getParameterTypeInfos())
            .map(parameterValueAssignees::assign)
            .map(v -> v.orElse(null))
            .toArray();

        WorkerResult workerResult = endPointTaskWorker.execute(parameterValues);
        log.info("WorkerResultType: `{}`, result: `{}`, result clazz: `{}`",
                 workerResult.getType(),
                 workerResult.getResult(),
                 Optional.ofNullable(workerResult.getResult()).map(Object::getClass).orElse(null));
        return workerResult;
    }

    public WorkerResult execute(RequestMethod method, PathUrl requestUrl, UrlParameterValues queryParameters, InputStream bodyInputStream) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(requestUrl);
        Objects.requireNonNull(queryParameters);
        Objects.requireNonNull(bodyInputStream);

        MatchedEndPointTaskWorker2 matchedEndPointTaskWorker = endPointTask.match(method, requestUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));

        // todo [review]
        // 받은 피드백 - annotation 모듈을 이용해서 동적으로 처리해라
        // 나의 구현 - 피드백대로 수행하지 않았다.
        // parameter 에 어노테이팅된 정보들을 여기서 해석하지 않고 생성시 미리 해석하는 방식으로 진행한다.
        // 해석된 정보를 이용하여 로직을 수행하기 때문에 annotation 과 연관관계를 끊을 수 있다.
        // 하지만 코드 적으로는 끊어졌지만, 개념적으로는 연결이 되어있다. 이것을 연관관계를 끊어다고 볼 수 있을까?
        // 임시저장 브랜치 - origin/split_annotation_module_role_at_MethodParameterValueMatcher
        UrlParameterValues pathVariableValue = new UrlParameterValues(matchedEndPointTaskWorker.getPathVariableValue().getValues());
        ParameterValueAssignees2 parameterValueAssignees = urlParameterValuesParameterValueAssignees2Function.apply(pathVariableValue);

        EndPointTaskWorker2 endPointTaskWorker = matchedEndPointTaskWorker.getEndPointTaskWorker();
        Object[] parameterValues = Arrays.stream(endPointTaskWorker.getParameterTypeInfos())
            .map(parameterValueAssignees::assign)
            .map(v -> v.orElse(null))
            .toArray();

        WorkerResult workerResult = endPointTaskWorker.execute(parameterValues);
        log.info("WorkerResultType: `{}`, result: `{}`, result clazz: `{}`",
                 workerResult.getType(),
                 workerResult.getResult(),
                 Optional.ofNullable(workerResult.getResult()).map(Object::getClass).orElse(null));
        return workerResult;
    }
}
