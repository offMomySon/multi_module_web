package executor;

import beanContainer.BeanContainer;
import java.lang.reflect.Method;
import java.util.Objects;
import mapper.HttpPathMatcherIf;
import variableExtractor.MethodConverter;
import variableExtractor.ParameterConverterFactory;
import vo.RequestBodyContent;
import vo.RequestMethod;
import vo.RequestParameters;
import static mapper.HttpPathMatcher.MatchedMethod;

public class RequestExecutor {
    private final BeanContainer container;
    private final HttpPathMatcherIf httpPathMatcher;

    public RequestExecutor(BeanContainer container, HttpPathMatcherIf httpPathMatcher) {
        Objects.requireNonNull(container, "beanContainer require not null.");
        Objects.requireNonNull(httpPathMatcher, "httpPathMatcher require not null.");
        this.container = container;
        this.httpPathMatcher = httpPathMatcher;
    }

    public Object execute(RequestMethod method, String url, RequestParameters formVariable, RequestBodyContent bodyContent) {
        MatchedMethod matchedMethod = httpPathMatcher.matchJavaMethod(method, url).orElseThrow(() -> new RuntimeException(""));

        Method javaMethod = matchedMethod.getJavaMethod();
        RequestParameters pathVariable = matchedMethod.getPathVariable();

        ParameterConverterFactory paramConverterFactory = new ParameterConverterFactory(formVariable, pathVariable, bodyContent);
        MethodConverter methodConverter = new MethodConverter(paramConverterFactory);
        MethodExecutor methodExecutor = new MethodExecutor(container, methodConverter);

        return methodExecutor.execute(javaMethod);
    }
}
