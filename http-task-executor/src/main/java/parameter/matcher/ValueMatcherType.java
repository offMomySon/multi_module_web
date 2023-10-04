package parameter.matcher;

import java.util.Arrays;
import java.util.Objects;

public enum ValueMatcherType {
    HTTP_URL,
    HTTP_QUERY_PARAM,
    HTTP_BODY,
    HTTP_INPUT_STREAM,
    HTTP_OUTPUT_STREAM;

    public static ValueMatcherType findByName(String findName){
        Objects.requireNonNull(findName);

        return Arrays.stream(values())
            .filter(v -> v.name().equalsIgnoreCase(findName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Does not exist ParameterType."));
    }

}
