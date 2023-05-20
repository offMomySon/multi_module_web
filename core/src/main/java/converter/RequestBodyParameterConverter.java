package converter;

import annotation.RequestBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import util.AnnotationUtils;
import vo.BodyContent;

public class RequestBodyParameterConverter implements ParameterConverter {
    private static final Class<RequestBody> REQUEST_BODY_CLASS = RequestBody.class;
    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    private final BodyContent bodyContent;

    public RequestBodyParameterConverter(BodyContent bodyContent) {
        if (Objects.isNull(bodyContent)) {
            throw new RuntimeException("requestBodyContent is null.");
        }
        this.bodyContent = bodyContent;
    }

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
            .map(bodyMessage -> createObject(parameter));
    }

    private Object createObject(Parameter parameter) {
        try {
            Class<?> type = parameter.getType();
            String bodyMessage = bodyContent.getValue();
            System.out.println(type);

            return JSON_MAPPER.readValue(bodyMessage, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json 을 파싱 할 수 없습니다. value : " + bodyContent.getValue(), e);
        }
    }
}
