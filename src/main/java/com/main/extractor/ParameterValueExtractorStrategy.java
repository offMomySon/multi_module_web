package com.main.extractor;

import com.main.util.AnnotationUtils;
import java.lang.reflect.Parameter;
import java.util.Objects;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestBody;
import matcher.annotation.RequestParam;
import matcher.converter.BodyContent;
import matcher.converter.RequestParameters;


// todo - 이전에 이런 패턴으로 작성했다가 빠꾸먹음.
// 의도.
// parameter 에 적용된 annotation 에 따라 값 추출자를 선택한다.
// 값 추출을 위한 모든 케이스를 관리하기 위해, 중앙집권식 이다.
// parameter 의 값 추출을 위해서는 알고리즘을 선택할 수 밖에 없기 때문에 필요한 객체라 생각된다.

// todo
// factory, strategy 차이가 무엇?
//
// factory
// 객체 생성에 관하여 복작한 로직이 있는경우, 생산에 대한 로직의 추상화, 캡슐화를 진행합니다.
// 입력받은 인자의 컨택스트에따라 다양한 형의 객체가 생성될 수 있습니다.
//
// strategy
// 동일한 역할을 수행하는 각각의 알고리즘에 대해서 추상화를 하고,
// 내부 알고리즘을 객체 내부로 숨김으로써 캡슐화를 합니다.
// 각각의 알고리즘을 추상화 함으로써 개별로 알고리즘을 관리할 수 있고, 이로 인해 추가,제거 가 용이합니다.
public class ParameterValueExtractorStrategy {
    private static final Class<?> PATH_VARIABLE = PathVariable.class;
    private static final Class<?> REQUEST_PARAM = RequestParam.class;
    private static final Class<?> REQUEST_BODY = RequestBody.class;

    private final RequestParameters pathVariableValue;
    private final RequestParameters queryParamValues;
    private final BodyContent bodyContent;

    public ParameterValueExtractorStrategy(RequestParameters pathVariableValue, RequestParameters queryParamValues, BodyContent bodyContent) {
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
            return new RequestParameterValueExtractor(PATH_VARIABLE, pathVariableValue, parameter);
        }

        if (AnnotationUtils.exist(parameter, REQUEST_PARAM)) {
            return new RequestParameterValueExtractor(REQUEST_PARAM, queryParamValues, parameter);
        }

        if (AnnotationUtils.exist(parameter, REQUEST_BODY)) {
            return new BodyParameterValueExtractor(bodyContent, parameter);
        }

        throw new RuntimeException("does not exit match strategy.");
    }
}
