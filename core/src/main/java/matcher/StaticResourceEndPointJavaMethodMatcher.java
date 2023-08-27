package matcher;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;

public class StaticResourceEndPointJavaMethodMatcher implements EndpointJavaMethodMatcher {
    private static final RequestMethod REQUEST_METHOD = RequestMethod.GET;

    private final PathUrl pathUrl;
    private final Method javaMethod;

    public StaticResourceEndPointJavaMethodMatcher(PathUrl pathUrl, Method javaMethod) {
        Objects.requireNonNull(pathUrl);
        Objects.requireNonNull(javaMethod);
        this.pathUrl = pathUrl;
        this.javaMethod = javaMethod;
    }

    @Override
    public Optional<MatchedMethod> match(RequestMethod requestMethod, PathUrl requestUrl) {
        boolean doesNotResourceMethod = !requestMethod.equals(REQUEST_METHOD);
        if (doesNotResourceMethod) {
            Optional.empty();
        }

        boolean doesNotEqualRequestUrl = pathUrl.equals(requestUrl);
        if(doesNotEqualRequestUrl){
            Optional.empty();
        }
        return Optional.of(new MatchedMethod(javaMethod, PathVariableValue.empty()));
    }
}
