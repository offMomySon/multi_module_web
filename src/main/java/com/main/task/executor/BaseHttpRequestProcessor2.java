package com.main.task.executor;

import executor.HttpRequestProcessor;
import java.text.SimpleDateFormat;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import matcher.MatchedEndPointTaskWorker;
import matcher.MatchedEndPointTaskWorker2;
import matcher.RequestMethod;
import matcher.segment.PathUrl;
import parameter.UrlParameters;
import parameter.extractor.HttpBodyParameterInfoExtractor;
import parameter.extractor.HttpUrlParameterInfoExtractor;
import task.EndPointTask2;
import task.worker.EndPointTaskWorker;
import task.worker.EndPointTaskWorker2;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.QueryParameters;

@Slf4j
public class BaseHttpRequestProcessor2 implements HttpRequestProcessor {
    private final EndPointTask2 endPointTask;
    private final HttpBodyParameterInfoExtractor httpBodyParameterInfoExtractor;
    private final HttpUrlParameterInfoExtractor requestParamHttpUrlParameterInfoExtractor;
    private final HttpUrlParameterInfoExtractor pathVariableParameterInfoExtractor;
    private final SimpleDateFormat simpleDateFormat;
    private final String hostAddress;

    public BaseHttpRequestProcessor2(EndPointTask2 endPointTask,
                                     HttpBodyParameterInfoExtractor httpBodyParameterInfoExtractor,
                                     HttpUrlParameterInfoExtractor requestParamHttpUrlParameterInfoExtractor,
                                     HttpUrlParameterInfoExtractor pathVariableParameterInfoExtractor,
                                     SimpleDateFormat simpleDateFormat,
                                     String hostAddress) {
        Objects.requireNonNull(endPointTask);
        Objects.requireNonNull(httpBodyParameterInfoExtractor);
        Objects.requireNonNull(requestParamHttpUrlParameterInfoExtractor);
        Objects.requireNonNull(pathVariableParameterInfoExtractor);
        Objects.requireNonNull(simpleDateFormat);
        Objects.requireNonNull(hostAddress);
        this.endPointTask = endPointTask;
        this.httpBodyParameterInfoExtractor = httpBodyParameterInfoExtractor;
        this.requestParamHttpUrlParameterInfoExtractor = requestParamHttpUrlParameterInfoExtractor;
        this.pathVariableParameterInfoExtractor = pathVariableParameterInfoExtractor;
        this.simpleDateFormat = simpleDateFormat;
        this.hostAddress = hostAddress;
    }

    @Override
    public boolean execute(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        RequestMethod method = RequestMethod.find(request.getHttpMethod().name());
        PathUrl requestUrl = PathUrl.from(request.getHttpRequestPath().getValue().toString());
        QueryParameters queryParameters = request.getQueryParameters();

        MatchedEndPointTaskWorker2 matchedEndPointTaskWorker = endPointTask.match(method, requestUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));
        EndPointTaskWorker2 endPointTaskWorker = matchedEndPointTaskWorker.getEndPointTaskWorker();

        UrlParameters pathVariableValue = new UrlParameters(matchedEndPointTaskWorker.getPathVariableValue().getValues());
        UrlParameters queryParamValues = new UrlParameters(queryParameters.getParameterMap());

        // todo [review]
        // 받은 피드백 - annotation 모듈을 이용해서 동적으로 처리해라
        // 나의 구현 - 피드백대로 수행하지 않았다.
        // parameter 에 어노테이팅된 정보들을 여기서 해석하지 않고 생성시 미리 해석하는 방식으로 진행한다.
        // 해석된 정보를 이용하여 로직을 수행하기 때문에 annotation 과 연관관계를 끊을 수 있다.
        // 하지만 코드 적으로는 끊어졌지만, 개념적으로는 연결이 되어있다. 이것을 연관관계를 끊어다고 볼 수 있을까?
        // 임시저장 브랜치 - origin/split_annotation_module_role_at_MethodParameterValueMatcher
//        ParameterValueMatchers parameterValueMatchers = new ParameterValueMatchers(
//            Map.of(HTTP_INPUT_STREAM, new SingleValueParameterValueMatcher<>(request.getBodyInputStream()),
//                   HTTP_OUTPUT_STREAM, new SingleValueParameterValueMatcher<>(response.getOutputStream()),
//                   HTTP_BODY, new HttpBodyParameterValueMatcher(httpBodyParameterInfoExtractor, request.getBodyInputStream()),
//                   HTTP_URL, new HttpUrlParameterValueMatcher(pathVariableParameterInfoExtractor, pathVariableValue),
//                   HTTP_QUERY_PARAM, new HttpUrlParameterValueMatcher(requestParamHttpUrlParameterInfoExtractor, queryParamValues)));
//        ParameterValueGetter parameterValueGetter = new ParameterValueGetter(parameterValueMatchers);
//
//        Object[] parameterValues = Arrays.stream(httpEndPointTask.getParameterTypeInfos())
//            .map(parameterValueGetter::get)
//            .map(v -> v.orElse(null))
//            .toArray();
//        Optional<HttpEndPointTask.HttpTaskResult> optionalResult = httpEndPointTask.execute(parameterValues);
//
//        log.info("methodResult : `{}`, clazz : `{}`", optionalResult.orElse(null), optionalResult.map(Object::getClass).orElse(null));
//
//        if (optionalResult.isEmpty()) {
//            return true;
//        }
//
//        HttpEndPointTask.HttpTaskResult httpTaskResult = optionalResult.get();
//        ContentType contentType = httpTaskResult.getContentType();
//        InputStream content = httpTaskResult.getContent();
//
//        HttpResponseHeaderCreator headerCreator = new HttpResponseHeaderCreator(simpleDateFormat, hostAddress, contentType);
//        HttpResponseHeader httpResponseHeader = headerCreator.create();
//
//        HttpResponseSender httpResponseSender = new HttpResponseSender(response);
//        httpResponseSender.send(httpResponseHeader, content);
        return true;
    }
}
