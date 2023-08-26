package com.main.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.task.converter.ParameterValueConverterFactory;
import com.main.task.value.BaseParameterValueMatcher;
import com.main.task.value.CompositeMethodParameterValueMatcher;
import com.main.task.value.HttpBodyAnnotationAnnotatedParameterValueMatcher;
import com.main.task.value.HttpUrlAnnotationAnnotatedParameterValueMatcher;
import com.main.task.value.ParameterValue;
import container.ObjectRepository;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import matcher.BaseEndpointJavaMethodMatcher;
import matcher.EndpointJavaMethodMatcher;
import matcher.RequestMethod;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestBody;
import matcher.annotation.RequestParam;
import matcher.converter.RequestParameters;
import matcher.converter.base.CompositeConverter;
import matcher.segment.PathUrl;
import processor.HttpRequestProcessor;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.HttpResponseWriter;
import vo.QueryParameters;

@Slf4j
public class BaseHttpRequestProcessor implements HttpRequestProcessor {
    private static final CompositeConverter converter = new CompositeConverter();
    private static final Objects EMPTY_VALUE = null;

    private final ObjectRepository objectRepository;
    private final EndpointJavaMethodMatcher endpointJavaMethodMatcher;

    public BaseHttpRequestProcessor(ObjectRepository objectRepository, EndpointJavaMethodMatcher endpointJavaMethodMatcher) {
        this.objectRepository = objectRepository;
        this.endpointJavaMethodMatcher = endpointJavaMethodMatcher;
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

    @Override
    public boolean execute(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        RequestMethod method = RequestMethod.find(request.getHttpMethod().name());
        PathUrl requestUrl = PathUrl.from(request.getHttpRequestPath().getValue().toString());
        QueryParameters queryParameters = request.getQueryParameters();

        BaseEndpointJavaMethodMatcher.MatchedMethod matchedMethod = endpointJavaMethodMatcher.match(method, requestUrl).orElseThrow(() -> new RuntimeException("Does not exist match method."));
        Method javaMethod = matchedMethod.getJavaMethod();
        RequestParameters pathVariableValue = new RequestParameters(matchedMethod.getPathVariableValue().getValues());
        RequestParameters queryParamValues = new RequestParameters(queryParameters.getParameterMap());

        CompositeMethodParameterValueMatcher methodParameterValueMatcher = new CompositeMethodParameterValueMatcher(
            Map.of(
                InputStream.class, new BaseParameterValueMatcher(request.getBodyInputStream()),
                RequestBody.class, new HttpBodyAnnotationAnnotatedParameterValueMatcher(request.getBodyInputStream()),
                PathVariable.class, new HttpUrlAnnotationAnnotatedParameterValueMatcher(PathVariable.class, pathVariableValue),
                RequestParam.class, new HttpUrlAnnotationAnnotatedParameterValueMatcher(RequestParam.class, queryParamValues))
        );

        ParameterValueGetter parameterValueGetter = new ParameterValueGetter(
            methodParameterValueMatcher,
            new ParameterValueConverterFactory(new ObjectMapper())
        );

        List<? extends ParameterValue<?>> parameterValues = Arrays.stream(javaMethod.getParameters())
            .map(parameterValueGetter::get)
            .collect(Collectors.toUnmodifiableList());

        MethodInvoker methodInvoker = new MethodInvoker(objectRepository);
        Optional<Object> methodResult = methodInvoker.invoke(javaMethod, parameterValues);
        if (methodResult.isEmpty()) {
            return false;
        }

        response.setStartLine("HTTP/1.1 200 OK");
        response.appendHeader(Map.of(
            "Date", "MON, 27 Jul 2023 12:28:53 GMT",
            "Host", "localhost:8080",
            "Content-Type", "text/html; charset=UTF-8"));
        HttpResponseWriter sender = response.getSender();

        InputStream inputStream = converter.convertToInputStream(methodResult.get());
        sender.send(inputStream);
        return true;
    }

    public interface HttpResponseCreator {
        HttpResponseView create();

        class HttpResponseView {
            private final String startLine;
            private final Map<String, String> header;

            public HttpResponseView(String startLine, Map<String, String> header) {
                Objects.requireNonNull(startLine);
                Objects.requireNonNull(header);
                if (startLine.isBlank()) {
                    throw new RuntimeException("startLine is empty.");
                }

                header.entrySet().stream()
                    .filter(e -> Objects.nonNull(e.getKey()))
                    .filter(e -> Objects.nonNull(e.getValue()))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

                if (header.isEmpty()) {
                    throw new RuntimeException("header is empty.");
                }

                this.startLine = startLine;
                this.header = header;
            }

            public String getStartLine() {
                return startLine;
            }

            public Map<String, String> getHeader() {
                return new HashMap<>(header);
            }
        }
    }

    private static String getFileExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        log.info("fileName : {}", fileName);

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }


    private static HttpResponse setHttpResponseHeader(HttpResponse response, String fileExtension) {
        log.info("fileExtension : {}", fileExtension);
        switch (fileExtension) {
            case "jpg":
                log.info("jpg : {}", fileExtension);
                response.setStartLine("HTTP/1.1 200 OK");
                response.appendHeader(Map.of(
                    "Date", "MON, 27 Jul 2023 12:28:53 GMT",
                    "Host", "localhost:8080",
                    "Connection", "close",
                    "Content-Type", "image/jpeg"));
                return response;
            case "txt":
                log.info("txt : {}", fileExtension);
                response.setStartLine("HTTP/1.1 200 OK");
                response.appendHeader(Map.of(
                    "Date", "MON, 27 Jul 2023 12:28:53 GMT",
                    "Host", "localhost:8080",
                    "Connection", "close",
                    "Content-Type", "text/html; charset=UTF-8"));
                return response;
        }
        throw new RuntimeException("does exist match fileExtension");
    }

    private static InputStream getResourceInputStream(Path resource) {
        try {
            return new BufferedInputStream(new FileInputStream(resource.toString()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}