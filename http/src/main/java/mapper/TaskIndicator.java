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
@ToString
public class TaskIndicator {
    private final String httpUrl;
    private final HttpMethod httpMethod;

    public TaskIndicator(String httpUrl, HttpMethod httpMethod) {
        this.httpUrl = ValidateUtil.validateNull(httpUrl);
        this.httpMethod = ValidateUtil.validateNull(httpMethod);
    }

    public TaskIndicator prevAppendUrl(String httpUrl){
        return new TaskIndicator(httpUrl + this.httpUrl, this.httpMethod);
    }

    public boolean isMatch(TaskIndicator givenIndicator) {
        if (this.httpMethod != givenIndicator.httpMethod) {
            return false;
        }

        String[] splitUrl = this.httpUrl.split("/");
        String[] splitGivenUrl = givenIndicator.httpUrl.split("/");

        if (splitUrl.length != splitGivenUrl.length) {
            return false;
        }

        for (int length = 0; length < splitUrl.length; length++) {
            String partOfUrl = splitUrl[length];
            String partOfGivenUrl = splitGivenUrl[length];

            if (isSkipAbleUrl(partOfUrl)) {
                continue;
            }

            if (doesNotMatchUrl(partOfUrl, partOfGivenUrl)) {
                return false;
            }
        }

        return true;
    }

    private static boolean doesNotMatchUrl(String partOfUrl, String partOfGivenUrl) {
        return !partOfUrl.equalsIgnoreCase(partOfGivenUrl);
    }

    private static boolean isSkipAbleUrl(String partOfUrl) {
        return partOfUrl.startsWith("{") && partOfUrl.startsWith("}");
    }

    public String getHttpUrl() {
        return this.httpUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskIndicator that = (TaskIndicator) o;
        return Objects.equals(httpUrl, that.httpUrl) && httpMethod == that.httpMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpUrl, httpMethod);
    }
}
