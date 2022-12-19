package request;

import java.text.MessageFormat;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public enum HttpMethod {
    POST, GET, DELETE, PUT;

    public static HttpMethod find(String name) {
        return Arrays.stream(values())
            .filter(value -> StringUtils.equalsIgnoreCase(value.name(), name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Not exist method. name = `{0}`", name)));
    }
}
