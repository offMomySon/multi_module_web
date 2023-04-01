package variableExtractor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MethodParamValueExtractor {
    private static final Objects DOES_NOT_EXIST_VALUE = null;

    private final ParamExtractor paramExtractor;
    private final Method method;

    public MethodParamValueExtractor(ParamExtractor paramExtractor, Method method) {
        this.paramExtractor = paramExtractor;
        this.method = method;
    }

    public Object[] extractValues() {
        List<Parameter> parameters = Arrays.stream(method.getParameters()).collect(Collectors.toUnmodifiableList());

        return parameters.stream()
            .map(paramExtractor::extractValue)
            .map(optionalObject -> optionalObject.orElse(DOES_NOT_EXIST_VALUE))
            .toArray();
    }
}
