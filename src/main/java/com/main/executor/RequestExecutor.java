package com.main.executor;

import container.Container;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import mapper.HttpPathMatcherIf;
import marker.PathVariable;
import marker.RequestBody;
import marker.RequestMethod;
import marker.RequestParam;
import variableExtractor.CompositeParameterConverter;
import variableExtractor.ParameterConverter;
import variableExtractor.RequestBodyParameterConverter;
import variableExtractor.RequestParameterConverter;
import vo.RequestBodyContent;
import vo.RequestValues;
import static mapper.HttpPathMatcher.MatchedMethod;

@Slf4j
public class RequestExecutor {
    private final Container container;
    private final HttpPathMatcherIf httpPathMatcher;

    public RequestExecutor(Container container, HttpPathMatcherIf httpPathMatcher) {
        Objects.requireNonNull(container, "beanContainer require not null.");
        Objects.requireNonNull(httpPathMatcher, "httpPathMatcher require not null.");
        this.container = container;
        this.httpPathMatcher = httpPathMatcher;
    }

    public Object execute(RequestMethod method, String url, RequestValues formVariable, RequestBodyContent bodyContent) {
        MatchedMethod matchedMethod = httpPathMatcher.matchJavaMethod(method, url).orElseThrow(() -> new RuntimeException(""));

        Method javaMethod = matchedMethod.getJavaMethod();
        RequestValues pathVariable = matchedMethod.getPathVariable();

        Map<Class<? extends Annotation>, ParameterConverter> classParameterConverterMap = Map.of(RequestParam.class, new RequestParameterConverter(RequestParam.class, formVariable),
                                                                                                 PathVariable.class, new RequestParameterConverter(PathVariable.class, pathVariable),
                                                                                                 RequestBody.class, new RequestBodyParameterConverter(bodyContent));
        CompositeParameterConverter compositeParameterConverter = new CompositeParameterConverter(classParameterConverterMap);

        MethodExecutor methodExecutor = new MethodExecutor(container, compositeParameterConverter);

        return methodExecutor.execute(javaMethod);
    }
}
