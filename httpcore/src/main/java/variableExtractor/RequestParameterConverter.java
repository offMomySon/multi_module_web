package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import mapper.AnnotationUtils;
import marker.RequestParam;
import vo.RequestParameters;

public class RequestParameterConverter implements ParameterConverter {
    private static final Class<RequestParam> REQUEST_PARAM_CLASS = RequestParam.class;

    private final RequestParameters requestParameters;

    public RequestParameterConverter(RequestParameters requestParameters) {
        if (Objects.isNull(requestParameters)) {
            throw new RuntimeException("requestParameters is null.");
        }
        this.requestParameters = requestParameters;
    }

    public Optional<Object> convertValue(Parameter parameter) {
        Optional<RequestParam> optionalRequestParam = AnnotationUtils.find(parameter, REQUEST_PARAM_CLASS);
        if (optionalRequestParam.isEmpty()) {
            return Optional.empty();
        }

        RequestParam requestParam = optionalRequestParam.get();

        String findName = Objects.isNull(requestParam.value()) ? parameter.getName() : requestParam.value();

        String objectOrNull = requestParameters.getOrDefault(findName, requestParam.defaultValue());

        boolean doesNotPossibleCreate = Objects.isNull(objectOrNull) && requestParam.required();
        if (doesNotPossibleCreate) {
            throw new RuntimeException("path value does not exist.");
        }

        return Optional.ofNullable(objectOrNull);
    }

}
