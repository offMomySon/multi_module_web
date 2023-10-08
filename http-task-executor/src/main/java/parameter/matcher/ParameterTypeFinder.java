package parameter.matcher;

import java.lang.reflect.Parameter;

public interface ParameterTypeFinder {
    ParameterValueAssigneeType find(Parameter parameter);
}
