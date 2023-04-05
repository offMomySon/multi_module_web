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
    private final BeanContainer beanContainer;
    private final HttpPathMatcherIf httpPathMatcher;

    public RequestExecutor(BeanContainer beanContainer, HttpPathMatcherIf httpPathMatcher) {
        Objects.requireNonNull(beanContainer, "beanContainer require not null.");
        Objects.requireNonNull(httpPathMatcher, "httpPathMatcher require not null.");
        this.beanContainer = beanContainer;
        this.httpPathMatcher = httpPathMatcher;
    }

    public Object execute(RequestMethod requestMethod, String requestUrl, RequestParameters formParameters, RequestBodyContent requestBodyContent) {
        MatchedMethod matchedMethod = httpPathMatcher.matchMethod(requestMethod, requestUrl).orElseThrow(() -> new RuntimeException(""));

        Method javaMethod = matchedMethod.getJavaMethod();
        RequestParameters pathVariable = matchedMethod.getPathVariable();

        ParameterConverterFactory converterFactory = new ParameterConverterFactory(formParameters, pathVariable, requestBodyContent);
        MethodConverter converter = new MethodConverter(converterFactory);
        MethodExecutor methodExecutor = new MethodExecutor(beanContainer, converter);

        return methodExecutor.execute(javaMethod);
    }
}
