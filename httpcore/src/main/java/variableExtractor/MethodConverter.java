package variableExtractor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MethodConverter {
    private static final Objects DOES_NOT_EXIST_VALUE = null;

    private final ParameterConverterFactory parameterConverterFactory;

    public MethodConverter(ParameterConverterFactory parameterConverterFactory) {
        Objects.requireNonNull(parameterConverterFactory, "parameterConverterFactory is null.");

        this.parameterConverterFactory = parameterConverterFactory;
    }

    public Object[] convertAsParameterValues(Method method) {
        Objects.requireNonNull(method);

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
