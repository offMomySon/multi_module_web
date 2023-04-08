package com.main.executor;

import container.Container;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import mapper.HttpPathMatcherIf;
import marker.PathVariable;
import marker.RequestMethod;
import marker.RequestParam;
import variableExtractor.CompositeParameterConverter;
import variableExtractor.RequestBodyParameterConverter;
import variableExtractor.RequestParameterConverter;
import vo.RequestBodyContent;
import vo.ParameterValues;
import static mapper.HttpPathMatcher.MatchedMethod;

public class RequestExecutor {
    private final Container container;
    private final HttpPathMatcherIf httpPathMatcher;

    public RequestExecutor(Container container, HttpPathMatcherIf httpPathMatcher) {
        Objects.requireNonNull(container, "beanContainer require not null.");
        Objects.requireNonNull(httpPathMatcher, "httpPathMatcher require not null.");
        this.container = container;
        this.httpPathMatcher = httpPathMatcher;
    }

    public Object execute(RequestMethod method, String url, ParameterValues formVariable, RequestBodyContent bodyContent) {
        MatchedMethod matchedMethod = httpPathMatcher.matchJavaMethod(method, url).orElseThrow(() -> new RuntimeException(""));

        Method javaMethod = matchedMethod.getJavaMethod();
        ParameterValues pathVariable = matchedMethod.getPathVariable();

        CompositeParameterConverter parameterConverter = new CompositeParameterConverter(List.of(new RequestParameterConverter(RequestParam.class, formVariable),
                                                                                                 new RequestParameterConverter(PathVariable.class, pathVariable),
                                                                                                 new RequestBodyParameterConverter(bodyContent)));
        MethodExecutor methodExecutor = new MethodExecutor(container, parameterConverter);

        return methodExecutor.execute(javaMethod);
    }
}
