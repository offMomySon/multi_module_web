package util;

import java.text.MessageFormat;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class ValidateUtil {
    public static void validate(String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isBlank(value)) {
            throw new RuntimeException(MessageFormat.format("value is invalid : `{}`", value));
        }
    }

    public static <T> void validateNull(T value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException(MessageFormat.format("value is null. clazz : `{}`", value.getClass()));
        }
    }
}
