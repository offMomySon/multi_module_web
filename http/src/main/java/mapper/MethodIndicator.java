package mapper;

import java.util.Objects;
import lombok.ToString;
import validate.ValidateUtil;
import vo.HttpMethod;

@ToString
public class MethodIndicator {
    private final String httpUrl;
    private final HttpMethod httpMethod;

    public MethodIndicator(String httpUrl, HttpMethod httpMethod) {
        this.httpUrl = ValidateUtil.validateNull(httpUrl);
        this.httpMethod = ValidateUtil.validateNull(httpMethod);
    }

    public boolean isMatch(MethodIndicator givenIndicator) {
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

            if (partOfUrl.startsWith("{") && partOfUrl.startsWith("}")) {
                continue;
            }

            if(!partOfUrl.equalsIgnoreCase(partOfGivenUrl)){
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodIndicator that = (MethodIndicator) o;
        return Objects.equals(httpUrl, that.httpUrl) && httpMethod == that.httpMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpUrl, httpMethod);
    }
}