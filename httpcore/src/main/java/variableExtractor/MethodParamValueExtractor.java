package variableExtractor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MethodParamValueExtractor {
    private static final Objects DOES_NOT_EXIST_VALUE = null;

    private final ParameterConverterFactory parameterConverterFactory;
    private final Method method;

    public MethodParamValueExtractor(ParameterConverterFactory parameterConverterFactory, Method method) {
        Objects.requireNonNull(parameterConverterFactory, "parameterConverterFactory is null.");
        Objects.requireNonNull(method, "method is null.");

        this.parameterConverterFactory = parameterConverterFactory;
        this.method = method;
    }

    public Object[] extractValues() {
        List<Object> objects = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            ParameterConverter converter = parameterConverterFactory.create(parameter);

            Optional<Object> optionalObject = converter.convertAsValue(parameter);
            Object valueOrNull = optionalObject.orElse(DOES_NOT_EXIST_VALUE);
            objects.add(valueOrNull);
        }
        return objects.toArray();
    }
}
