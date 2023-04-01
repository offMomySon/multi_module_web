package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import mapper.AnnotationUtils;
import marker.RequestParam;

public class RequestParamValueExtractor implements ParamExtractor {
    private static final Class<RequestParam> REQUEST_PARAM_CLASS = RequestParam.class;

    private final Map<String, String> requestParams;

    public RequestParamValueExtractor(Map<String, String> requestParams) {
        this.requestParams = requestParams;
    }

    public Optional<Object> extractValue(Parameter parameter) {
        Optional<RequestParam> optionalRequestParam = AnnotationUtils.find(parameter, REQUEST_PARAM_CLASS);
        if (optionalRequestParam.isEmpty()) {
            return Optional.empty();
        }

        RequestParam requestParam = optionalRequestParam.get();

        String findName = Objects.isNull(requestParam.value()) ? parameter.getName() : requestParam.value();

        Object objectOrNull = requestParams.getOrDefault(findName, requestParam.defaultValue());

        if (Objects.isNull(objectOrNull) && requestParam.required()) {
            throw new RuntimeException("path value does not exist.");
        }

        return Optional.ofNullable(objectOrNull);
    }

}
