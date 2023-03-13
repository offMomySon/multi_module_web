package mapper;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
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
public class MethodMatcher2 {
    private final HttpMethod httpMethod;
    private final String url;
    private final Method javaMethod;

    public MethodMatcher2(HttpMethod httpMethod, String url, Method javaMethod) {
        if (Objects.isNull(httpMethod)) {
            throw new RuntimeException("not valid httpMethod");
        }
        if (Objects.isNull(url) || url.isBlank() || url.isEmpty()) {
            throw new RuntimeException("not valid methodUri");
        }

        this.httpMethod = ValidateUtil.validateNull(httpMethod);
        this.url = ValidateUtil.validate(url);
        this.javaMethod = javaMethod;
    }

    public Optional<Method> matchByHttp(HttpMethod httpMethod, String requsetUrl) {
        if(!Objects.equals(this.httpMethod, httpMethod)){
            return Optional.empty();
        }
        if(Objects.isNull(requsetUrl) || requsetUrl.isBlank() || requsetUrl.isEmpty()){
            return Optional.empty();
        }

        String[] splitUrl = this.url.split("/");
        String[] splitOtherUrl = requsetUrl.split("/");

        // TODO - 길이가 같지 않을 수 있슴.
        // /java/foo
        // /java/foo/../foo
//        if (splitUrl.length != splitOtherUrl.length) {
//            return Optional.empty();
//        }

        for (int length = 0; length < splitUrl.length; length++) {
            String partOfUrl = splitUrl[length];
            String partOfOtherUrl = splitOtherUrl[length];

            if (isSkipAbleUrl(partOfUrl)) {
                continue;
            }

            if (doesNotMatchUrl(partOfUrl, partOfOtherUrl)) {
                return Optional.empty();
            }
        }
        return Optional.of(javaMethod);
    }


    private static boolean doesNotMatchUrl(String partOfUrl, String partOfGivenUrl) {
        return !partOfUrl.equalsIgnoreCase(partOfGivenUrl);
    }

    private static boolean isSkipAbleUrl(String partOfUrl) {
        return partOfUrl.startsWith("{") && partOfUrl.endsWith("}");
    }

}
