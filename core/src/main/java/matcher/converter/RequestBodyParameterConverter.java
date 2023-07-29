package matcher.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import matcher.annotation.RequestBody;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import util.AnnotationUtils;

@Slf4j
public class RequestBodyParameterConverter implements ParameterConverter {
    private static final Class<RequestBody> REQUEST_BODY_CLASS = RequestBody.class;
    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    private final BodyContent bodyContent;

//    생성자.
//    bodyContent 를 받는다.
    public RequestBodyParameterConverter(BodyContent bodyContent) {
        if (Objects.isNull(bodyContent)) {
            throw new RuntimeException("requestBodyContent is null.");
        }
        this.bodyContent = bodyContent;
    }

//    1. parameter 를 받는다.
//    2. parameter 의 annotation 중에서 RequestBody 을 가져온다.
//    3. requestBody 가 존재하지 않으면 exception 이 발생시킨다.
//    4. requestBody 를 가져온다.
//    5. bodyContent 의 빈값 여부를 가져온다.

//    6. requestBody 가 반드시 필요하고, bodyContent 가 빈값이라면 exeption 을 발생시킨다.
//    7. requestBody 가 반드시 필요하지 않고, bodyContent 가 빈값이라면 빈값을 반환한다.
//    8. bodyContent 를 parameter type 에 따라 변환한다.
    public Optional<Object> convertAsValue(Parameter parameter) {
        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, REQUEST_BODY_CLASS);
        if (optionalRequestBody.isEmpty()) {
            throw new IllegalArgumentException("requestBody 만 받을 수 있습니다.");
        }

        RequestBody requestBody = optionalRequestBody.get();
        boolean isEmptyBody = bodyContent.isEmpty();

        boolean doesNotPossibleCreate = requestBody.required() && isEmptyBody;
        if (doesNotPossibleCreate) {
            throw new RuntimeException("메세지가 비어 생성할 수 없습니다.");
        }

        boolean isPossibleEmptyBody = !requestBody.required() && isEmptyBody;
        if (isPossibleEmptyBody) {
            return Optional.empty();
        }

        return Optional.ofNullable(bodyContent.getValue())
            .map(bodyMessage -> createObject(parameter, bodyMessage));
    }

    private Object createObject(Parameter parameter, String bodyMessage) {
        try {
            Class<?> type = parameter.getType();
            log.info("type : `{}`", type);
            log.info("bodyMessage : `{}`", bodyMessage);

            return JSON_MAPPER.readValue(bodyMessage, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json 을 파싱 할 수 없습니다. value : " + bodyContent.getValue(), e);
        }
    }
}
