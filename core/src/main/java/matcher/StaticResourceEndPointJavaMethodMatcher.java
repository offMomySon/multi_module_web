package matcher;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;

public class StaticResourceEndPointJavaMethodMatcher implements EndpointJavaMethodMatcher {
    private static final RequestMethod REQUEST_METHOD = RequestMethod.GET;

    private final PathUrl pathUrl;

    public StaticResourceEndPointJavaMethodMatcher(PathUrl pathUrl) {
        Objects.requireNonNull(pathUrl);
        this.pathUrl = pathUrl;
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

        return Optional.of(new MatchedMethod(getStaticResourceFindMethod(), PathVariableValue.empty()));
    }

    private Method getStaticResourceFindMethod(){
        try {
            return StaticResourceFinder.class.getMethod("find");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }
}
