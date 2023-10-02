package com.main.task.executor;

import annotation.PathVariable;
import annotation.RequestBody;
import annotation.RequestParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.task.matcher.HttpBodyAnnotationAnnotatedParameterValueMatcher;
import com.main.task.response.HttpResponseSender;
import executor.HttpRequestProcessor;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import matcher.EndpointTaskMatcher;
import matcher.MatchedEndPoint;
import matcher.RequestMethod;
import matcher.segment.PathUrl;
import parameter.BaseParameterValueMatcher;
import parameter.CompositeMethodParameterValueMatcher;
import parameter.HttpUrlAnnotationAnnotatedParameterValueMatcher;
import parameter.MethodParameterValueMatcher;
import parameter.ParameterValueClazzConverterFactory;
import parameter.ParameterValueGetter;
import parameter.RequestParameters;
import response.HttpResponseHeader;
import response.HttpResponseHeaderCreator;
import task.HttpEndPointTask;
import task.HttpEndPointTask.HttpTaskResult;
import vo.ContentType;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.QueryParameters;

@Slf4j
public class BaseHttpRequestProcessor implements HttpRequestProcessor {

    private final EndpointTaskMatcher endpointTaskMatcher;
    private final SimpleDateFormat simpleDateFormat;
    private final String hostAddress;

    public BaseHttpRequestProcessor(EndpointTaskMatcher endpointTaskMatcher, SimpleDateFormat simpleDateFormat, String hostAddress) {
        Objects.requireNonNull(endpointTaskMatcher);
        Objects.requireNonNull(simpleDateFormat);
        Objects.requireNonNull(hostAddress);
        this.endpointTaskMatcher = endpointTaskMatcher;
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

        MatchedEndPoint matchedEndPoint = endpointTaskMatcher.match(method, requestUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));
        HttpEndPointTask httpEndPointTask = matchedEndPoint.getHttpEndPointTask();

        RequestParameters pathVariableValue = new RequestParameters(matchedEndPoint.getPathVariableValue().getValues());
        RequestParameters queryParamValues = new RequestParameters(queryParameters.getParameterMap());

        // todo [review]
        // 받은 피드백 - annotation 모듈을 이용해서 동적으로 처리해라
        // 나의 구현 - 피드백대로 수행하지 않았다.
        // parameter 에 어노테이팅된 정보들을 여기서 해석하지 않고 생성시 미리 해석하는 방식으로 진행한다.
        // 해석된 정보를 이용하여 로직을 수행하기 때문에 annotation 과 연관관계를 끊을 수 있다.
        // 하지만 코드 적으로는 끊어졌지만, 개념적으로는 연결이 되어있다. 이것을 연관관계를 끊어다고 볼 수 있을까?
        // 임시저장 브랜치 - origin/split_annotation_module_role_at_MethodParameterValueMatcher
        MethodParameterValueMatcher methodParameterValueMatcher = new CompositeMethodParameterValueMatcher(
            Map.of(InputStream.class, new BaseParameterValueMatcher<>(request.getBodyInputStream()),
                   RequestBody.class, new HttpBodyAnnotationAnnotatedParameterValueMatcher(request.getBodyInputStream()),
                   PathVariable.class, new HttpUrlAnnotationAnnotatedParameterValueMatcher<>(PathVariable.class, pathVariableValue),
                   RequestParam.class, new HttpUrlAnnotationAnnotatedParameterValueMatcher<>(RequestParam.class, queryParamValues))
        );

        ParameterValueGetter parameterValueGetter = new ParameterValueGetter(methodParameterValueMatcher);
        Object[] parameterValues = Arrays.stream(httpEndPointTask.getExecuteParameters())
            .map(parameterValueGetter::get)
            .map(v -> v.orElse(null))
            .toArray();
        Optional<HttpTaskResult> optionalResult = httpEndPointTask.execute(parameterValues);

        log.info("methodResult : `{}`, clazz : `{}`", optionalResult.orElse(null), optionalResult.map(Object::getClass).orElse(null));

        if (optionalResult.isEmpty()) {
            return true;
        }

        HttpTaskResult httpTaskResult = optionalResult.get();
        ContentType contentType = httpTaskResult.getContentType();
        InputStream content = httpTaskResult.getContent();

        HttpResponseHeaderCreator headerCreator = new HttpResponseHeaderCreator(simpleDateFormat, hostAddress, contentType);
        HttpResponseHeader httpResponseHeader = headerCreator.create();

        HttpResponseSender httpResponseSender = new HttpResponseSender(response);
        httpResponseSender.send(httpResponseHeader, content);
        return true;
    }
}

//    1. http request, response 를 받는다.
//    2. http request method 를 RequestMethod 로 변환한다.
//    3. http request path 를 PathUrl 로 변환한다.
//    4. http request query param 을 queryParameters 로 변환한다.
//    5. http body inputstream 을 body content 로 변환한다.

//    6. requestMethod, PathUrl 와 매칭되는 method 를 가져온다. - 복잡도가 높다.
//      1. requestMethod, PathUrl 을 받는다.
//      2. request PathUrl 이 null 이면 empty 값을 반환합니다.
//      3. requestMethod 가 instance variable requestMethod 와 일치하지 않으면 빈값을 반환한다.
//      4. request PathUrl 이 pathUrlMatcher 와 일치하는지 체크한다, 일치하면 PathVariableValue 를 가져온다.
//      5. java method, pathVariableValue 를 반환한다.


//    7. pathVariableValue 를 requestParameter 로 변환한다.
//    8. queryParameters 를 requestParameter 로 변환한다.
//    9. parameter 가 PathVariable 어노테이션이면 값으로 변환하는 converter 를 생성한다. - 복잡도가 높다.
//      1. parameter 를 받는다.
//      2. parameter 의 annotation 중에서 관심이 있는 annotation 이 존재하는지 확인한다.
//      3. annotation 으로부터 annotation value 를 가져온다.
//      4. parameter name 을 가져온다.
//      5. parameter name 이 존재하지 않으면 annotation value 의 name 을 가져온다.
//      6. annotation value 로 부터 deafult value 를 가져온다.
//      7. requestParam 으로 부터 parameter 이름에 일치하는 값을 가져온다. 존재하지 않으면 기본 값을 가져온다. ( - foundValue )
//      8. 어노테이션 값이 반드시 필요하고 foundValue 가 존재하지 않으면 exception 이 발생한다.
//      9. foundValue 가 존재하지 않으면 empty 값을 반환한다.
//      10. foundValue 를 parameter type 에 따라 변환이 필요하지 않으면 그대로 반환한다.
//      11. foundValue 를 parameter type 에 따라 변환한다.
//    10. parameter 가 RequestParam 어노테이션이면 값으로 변환하는 converter 를 생성한다.
//    11. parameter 가 RequestBody 어노테이션이면 값으로 변환하는 converter 를 생성한다.
//      1. parameter 를 받는다.
//      2. parameter 의 annotation 중에서 RequestBody 을 가져온다.
//      3. requestBody 가 존재하지 않으면 exception 이 발생시킨다.
//      4. requestBody 를 가져온다.
//      5. bodyContent 의 빈값 여부를 가져온다.
//      6. requestBody 가 반드시 필요하고, bodyContent 가 빈값이라면 exeption 을 발생시킨다.
//      7. requestBody 가 반드시 필요하지 않고, bodyContent 가 빈값이라면 빈값을 반환한다.
//      8. bodyContent 를 parameter type 에 따라 변환한다.
//    12. 3개의 converter 를 하나의 converter 로 변환한다.

//    13. method 에서 class 를 추출한다.
//    14. objectRepository 에서 class 에 대한 instance 를 추출한다.
//    15. java method 에서 parameter 들을 가져온다.
//    16. parameter 를 value 로 변환한다.  - 복잡도가 높다.
//    17. 이것을 n 번 실행하여 모든 parameter 를 value 로 변환한다.
//    18. instance, javamethod, parameter values 로 method 를 실행하고 결과값을 가져온다.

//    19. 결과 값을 inputStream 으로 변환한다.
//    20. http response startLine 을 셋팅한다.
//    21. http response header 을 셋팅한다.
//    22. http response 로 부터 http response writer 를 가져온다.
//    23. http response writer 로 http header, body 를 전송한다.

//    키워드 정리.
//    1. endpoint(=http method, url).
//       역할 - http method, url 에 매칭되는 method 를 찾아온다.
//    2. parameter signature assignee 생성.
//      역할 - requestParam 으로 부터 parameter signature(class type, annotation) 에 따라 parameter signature assignee 를 생성한다.
//    3. parameter signature value
//      역할 - signature 별 parameter 에 할당할 값을 선택한다.
//    4. parameter value type
//      역할 - parameter type 별로 value type 을 변환한다.
//    5. method execute.
//       역할 - method 를 실행하고 결과를 가져온다.
//    6. http response header value.
//       역할 - object 에 따라 적절한 http response header value 를 생성한다.
//    7. http response sender.
//       역할 - http response header, http response body 를 전송한다.

//    1. EndpointMethodMatcher
//      개념 - method, url 에 매칭되는 method 를 찾아온다.
//    2. MethodParameterValueMatcher
//      개념 - http request value 로 부터 method parameter 마다의 value 들을 매칭한다.
//    3. ParameterValueConverter
//      개념 - parameter type 별로 value type 을 변환한다.
//    4. MethodInvoker
//      개념 - method 를 가진 instance 를 불러와 실행시킨다.
//    5. HttpResponseHeaderValueCreator
//      개념 - Object 에 따라 response header value 를 생성한다.
//    6. HttpResponseSender
//      개념 - http response 응답을 전송한다.