package mapper;

import lombok.ToString;
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
    private final String httpUrl;

    public MethodIndicator(HttpMethod httpMethod, String httpUrl) {
        this.httpMethod = ValidateUtil.validateNull(httpMethod);
        this.httpUrl = ValidateUtil.validate(httpUrl);
    }

    public String getHttpUrl() {
        return this.httpUrl;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        MethodIndicator otherIndicator = (MethodIndicator) other;

        if (this.httpMethod != otherIndicator.httpMethod) {
            return false;
        }

        String[] splitUrl = this.httpUrl.split("/");
        String[] splitOtherUrl = otherIndicator.httpUrl.split("/");

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
