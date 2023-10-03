package parameter.matcher;

import java.lang.reflect.Parameter;

public interface ParameterTypeFinder {
    ParameterType find(Parameter parameter);
}
