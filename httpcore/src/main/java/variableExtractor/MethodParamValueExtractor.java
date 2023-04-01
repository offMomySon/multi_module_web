package variableExtractor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MethodParamValueExtractor {
    private static final Objects DOES_NOT_EXIST_VALUE = null;

    private final ParameterConverter parameterConverter;
    private final Method method;

    public MethodParamValueExtractor(ParameterConverter parameterConverter, Method method) {
        this.parameterConverter = parameterConverter;
        this.method = method;
    }

    public Object[] extractValues() {
        List<Parameter> parameters = Arrays.stream(method.getParameters()).collect(Collectors.toUnmodifiableList());

        return parameters.stream()
            .map(parameterConverter::convertValue)
            .map(optionalObject -> optionalObject.orElse(DOES_NOT_EXIST_VALUE))
            .toArray();
    }
}
