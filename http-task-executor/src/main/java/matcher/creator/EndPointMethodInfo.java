package matcher.creator;

import java.lang.reflect.Method;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import matcher.RequestMethod;

@EqualsAndHashCode
@Getter
public class EndPointMethodInfo {
    private final RequestMethod requestMethod;
    private final String url;
    private final Object object;
    private final Method javaMethod;

    public EndPointMethodInfo(RequestMethod requestMethod, String url, Object object, Method javaMethod) {
        if (Objects.isNull(requestMethod) ||
            Objects.isNull(url) || url.isBlank() || url.isBlank() ||
            Objects.isNull(object) ||
            Objects.isNull(javaMethod)) {
            throw new RuntimeException("value is invalid.");
        }

        this.requestMethod = requestMethod;
        this.url = url;
        this.object = object;
        this.javaMethod = javaMethod;
    }
}
