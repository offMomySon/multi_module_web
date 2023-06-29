package com.main.extractor;

import com.main.util.AnnotationUtils;
import java.lang.reflect.Parameter;
import java.util.Objects;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestBody;
import matcher.annotation.RequestParam;
import matcher.converter.BodyContent;
import matcher.converter.RequestParameters;

public class ValueExtractorStrategy {
    private static final Class<?> PATH_VARIABLE = PathVariable.class;
    private static final Class<?> REQUEST_PARAM = RequestParam.class;
    private static final Class<?> REQUEST_BODY = RequestBody.class;

    private final RequestParameters pathVariableValue;
    private final RequestParameters queryParamValues;
    private final BodyContent bodyContent;

    public ValueExtractorStrategy(RequestParameters pathVariableValue, RequestParameters queryParamValues, BodyContent bodyContent) {
        Objects.requireNonNull(pathVariableValue);
        Objects.requireNonNull(queryParamValues);
        Objects.requireNonNull(bodyContent);
        this.pathVariableValue = pathVariableValue;
        this.queryParamValues = queryParamValues;
        this.bodyContent = bodyContent;
    }

    public ParameterValueExtractor create(Parameter parameter) {
        Objects.requireNonNull(parameter);

        if (AnnotationUtils.exist(parameter, PATH_VARIABLE)) {
            return new BaseParameterValueExtractor(PATH_VARIABLE, pathVariableValue, parameter);
        }

        if (AnnotationUtils.exist(parameter, REQUEST_PARAM)) {
            return new BaseParameterValueExtractor(REQUEST_PARAM, queryParamValues, parameter);
        }

        if (AnnotationUtils.exist(parameter, REQUEST_BODY)) {
            return new BodyParameterValueExtractor(bodyContent, parameter);
        }

        throw new RuntimeException("does not exit match strategy.");
    }
}
