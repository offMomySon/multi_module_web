package parameter.matcher;

import java.lang.reflect.Parameter;

public interface ParameterTypeFinder {
    ValueMatcherType find(Parameter parameter);
}
