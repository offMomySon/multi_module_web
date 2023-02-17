package mapper;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.ToString;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import validate.ValidateUtil;
import vo.HttpMethod;

// TODO methodIndicator 간의 중복제거를 어떻게 할 것인가?
// annotation 을 통해서 생성된 methodIndicator 의 중복제거 고려가 필요합니다.
// e.g)
//      /request/{pathVariable}
//      /request/genericPath
//      /request/{anotherPathVariable}
@ToString
public class MethodIndicator {
    private final HttpMethod httpMethod;
    private final String methodUri;

    public MethodIndicator(HttpMethod httpMethod, String methodUri) {
        this.httpMethod = ValidateUtil.validateNull(httpMethod);
        this.methodUri = ValidateUtil.validate(methodUri);
    }

    public static MethodIndicator from(HttpMethod httpMethod, String clazzUrl, String methodUrl) {
        if (Objects.isNull(httpMethod)){
            throw new RuntimeException("httpMethod is null.");
        }
        if(Objects.isNull(clazzUrl)){
            throw new RuntimeException("controllerUrl is null.");
        }
        if (Objects.isNull(methodUrl)) {
            throw new RuntimeException("methodUrl is null.");
        }
        if (Objects.isNull(httpMethod)) {
            throw new RuntimeException("httpMethod is null.");
        }

        if (clazzUrl.isEmpty() || clazzUrl.isBlank()) {
            throw new RuntimeException(MessageFormat.format("controller url is not valid value : {0}", clazzUrl));
        }
        if (methodUrl.isEmpty() || methodUrl.isBlank()) {
            throw new RuntimeException(MessageFormat.format("methodUrl is not valid value : {0}", methodUrl));
        }

        String methodUri = clazzUrl + methodUrl;
        return new MethodIndicator(httpMethod, methodUri);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        MethodIndicator otherIndicator = (MethodIndicator) other;

        if (this.httpMethod != otherIndicator.httpMethod) {
            return false;
        }

        String[] splitUrl = this.methodUri.split("/");
        String[] splitOtherUrl = otherIndicator.methodUri.split("/");

        if (splitUrl.length != splitOtherUrl.length) {
            return false;
        }

        for (int length = 0; length < splitUrl.length; length++) {
            String partOfUrl = splitUrl[length];
            String partOfOtherUrl = splitOtherUrl[length];

            if (isSkipAbleUrl(partOfUrl)) {
                continue;
            }

            if (doesNotMatchUrl(partOfUrl, partOfOtherUrl)) {
                return false;
            }
        }
        return true;
    }


    private static boolean doesNotMatchUrl(String partOfUrl, String partOfGivenUrl) {
        return !partOfUrl.equalsIgnoreCase(partOfGivenUrl);
    }

    private static boolean isSkipAbleUrl(String partOfUrl) {
        return partOfUrl.startsWith("{") && partOfUrl.endsWith("}");
    }

    private static <T> T validateEmpty(T value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException(MessageFormat.format("value is null. `type`/`value` - `{0}`/`{1}`", value.getClass().getSimpleName(), value));
        }
        return value;
    }
}
