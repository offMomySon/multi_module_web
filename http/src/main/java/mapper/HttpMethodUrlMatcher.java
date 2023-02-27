package mapper;

import java.util.Objects;
import lombok.ToString;
import validate.ValidateUtil;
import vo.HttpMethod;

// TODO methodIndicator 간의 중복제거를 어떻게 할 것인가?
// annotation 을 통해서 생성된 methodIndicator 의 중복제거 고려가 필요합니다.
// e.g)
//      /request/{pathVariable}
//      /request/genericPath
//      /request/{anotherPathVariable}

/**
 * httpMethodUrlMatcher 간의 매칭을 수행합니다.
 */
@ToString
public class HttpMethodUrlMatcher implements Matcher {
    private final HttpMethod httpMethod;
    private final String methodUri;

    public HttpMethodUrlMatcher(HttpMethod httpMethod, String methodUri) {
        if (Objects.isNull(httpMethod)) {
            throw new RuntimeException("not valid httpMethod");
        }
        if (Objects.isNull(methodUri) || methodUri.isBlank() || methodUri.isEmpty()) {
            throw new RuntimeException("not valid methodUri");
        }

        this.httpMethod = ValidateUtil.validateNull(httpMethod);
        this.methodUri = ValidateUtil.validate(methodUri);
    }

    public boolean doesNotMatch(Matcher otherMatcher) {
        return !match(otherMatcher);
    }

    @Override
    public boolean match(Matcher _otherMatcher) {
        if (Objects.isNull(_otherMatcher) || !Objects.equals(this.getClass(), _otherMatcher.getClass())) {
            return false;
        }
        HttpMethodUrlMatcher otherMatcher = (HttpMethodUrlMatcher) _otherMatcher;

        if (this.httpMethod != otherMatcher.httpMethod) {
            return false;
        }

        String[] splitUrl = this.methodUri.split("/");
        String[] splitOtherUrl = otherMatcher.methodUri.split("/");

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

}
