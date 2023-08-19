package com.main.task.policy;

import com.main.util.AnnotationUtils;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestBody;
import matcher.annotation.RequestParam;

public class ParameterValuePolicyFactory {
    public static ParameterRequireChecker create(Parameter parameter, Optional<Object> value) {
        Objects.requireNonNull(parameter);
        Objects.requireNonNull(value);

        boolean existRequestParameter = AnnotationUtils.exist(parameter, RequestParam.class);
        if (existRequestParameter) {
//            return HttpUrlRequireChecker.from(parameter, RequestParam.class, value.get());
        }

        boolean existPathVariableParameter = AnnotationUtils.exist(parameter, PathVariable.class);
        if (existPathVariableParameter) {
//            return HttpUrlRequireChecker.from(parameter, PathVariable.class, value.get());
        }

        boolean existRequestBody = AnnotationUtils.exist(parameter, RequestBody.class);
        if (existRequestBody) {
//            return HttpBodyAnnotationAnnotatedParameterRequireChecker.from(parameter, value.get());
        }
        return new NoneAnnotatedParameterRequireChecker(parameter, value.get());
    }
}
