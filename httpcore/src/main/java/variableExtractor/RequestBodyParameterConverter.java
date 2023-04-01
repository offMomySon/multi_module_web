package variableExtractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import mapper.AnnotationUtils;
import marker.RequestBody;

public class RequestBodyParameterConverter implements ParameterConverter {
    private static final Class<RequestBody> REQUEST_BODY_CLASS = RequestBody.class;
    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    private final String bodyMessage;

    public RequestBodyParameterConverter(String bodyMessage) {
        this.bodyMessage = bodyMessage;
    }

    public Optional<Object> convertValue(Parameter parameter) {
        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, REQUEST_BODY_CLASS);
        if (optionalRequestBody.isEmpty()) {
            return Optional.empty();
        }

        RequestBody requestBody = optionalRequestBody.get();

        boolean isEmptyBody = Objects.isNull(bodyMessage) || bodyMessage.isEmpty() || bodyMessage.isBlank();

        boolean doesNotPossibleCreate = requestBody.required() && isEmptyBody;
        if (doesNotPossibleCreate) {
            throw new RuntimeException("메세지가 비어 생성할 수 없습니다.");
        }

        boolean isPossibleEmptyBody = !requestBody.required() && isEmptyBody;
        if (isPossibleEmptyBody) {
            return Optional.empty();
        }

        return Optional.ofNullable(bodyMessage)
            .map(bodyMessage -> createObject(parameter));
    }

    private Object createObject(Parameter parameter) {
        try {
            Class<?> type = parameter.getType();
            return JSON_MAPPER.readValue(bodyMessage, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json 을 파싱 할 수 없습니다. value : " + bodyMessage, e);
        }
    }
}
