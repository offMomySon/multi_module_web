package request;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public enum Method {
    GET, POST, PUT, PATCH, DELETE;

    public static Optional<Method> find(String name){
        return Arrays.stream(values())
            .filter(value -> StringUtils.equalsIgnoreCase(value.name(), name))
            .findFirst();
    }
}
