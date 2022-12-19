package validate;

import java.text.MessageFormat;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class ValidateUtil {
    public static boolean isValid(String value) {
        if(StringUtils.isEmpty(value) || StringUtils.isBlank(value)){
            return false;
        }
        return true;
    }

    public static String validate(String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isBlank(value)) {
            throw new RuntimeException(MessageFormat.format("value is invalid : `{}`", value));
        }
        return value;
    }

    public static <T> T validateNull(T value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException(MessageFormat.format("value is null. clazz : `{}`", value.getClass()));
        }
        return value;
    }
}
