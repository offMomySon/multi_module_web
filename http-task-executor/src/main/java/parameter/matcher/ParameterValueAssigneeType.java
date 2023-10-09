package parameter.matcher;

import java.util.Arrays;
import java.util.Objects;

public enum ParameterValueAssigneeType {
    URL,
    QUERY_PARAM,
    BODY,
    INPUT_STREAM,
    OUTPUT_STREAM;

    public static ParameterValueAssigneeType findByName(String findName){
        Objects.requireNonNull(findName);

        return Arrays.stream(values())
            .filter(v -> v.name().equalsIgnoreCase(findName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Does not exist ParameterType."));
    }

}
