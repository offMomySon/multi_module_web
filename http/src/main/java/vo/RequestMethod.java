package vo;

import java.text.MessageFormat;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public enum RequestMethod {
    POST, GET, DELETE, PUT;

    public static RequestMethod find(String name) {
        return Arrays.stream(values())
            .filter(value -> StringUtils.equalsIgnoreCase(value.name(), name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Not exist method. name = `{0}`", name)));
    }
}
