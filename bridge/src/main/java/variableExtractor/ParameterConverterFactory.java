package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import marker.PathVariable;
import marker.RequestBody;
import marker.RequestParam;
import util.AnnotationUtils;
import vo.ParamAnnotationValue;
import vo.ParameterValues;
import vo.RequestBodyContent;


public class ParameterConverterFactory {
    private final ParameterValues formParams;
    private final ParameterValues pathParams;
    private final RequestBodyContent requestBodyContent;

    public ParameterConverterFactory(ParameterValues formParams, ParameterValues pathParams, RequestBodyContent requestBodyContent) {
        if (Objects.isNull(formParams) || Objects.isNull(pathParams) || Objects.isNull(requestBodyContent)) {
            throw new RuntimeException("parameter is null.");
        }

        this.formParams = formParams;
        this.pathParams = pathParams;
        this.requestBodyContent = requestBodyContent;
    }

    public ParameterConverter create(Parameter parameter) {
        if (Objects.isNull(parameter)) {
            throw new RuntimeException("parameter is null.");
        }

        Optional<RequestParam> optionalRequestParam = AnnotationUtils.find(parameter, RequestParam.class);
        if (optionalRequestParam.isPresent()) {
            RequestParam requestParam = optionalRequestParam.get();
            ParamAnnotationValue requestParamAnnotationValue = ParamAnnotationValue.from(requestParam);

            return new RequestParameterConverter(requestParam.annotationType(), formParams);
        }

        Optional<PathVariable> optionalPathVariable = AnnotationUtils.find(parameter, PathVariable.class);
        if (optionalPathVariable.isPresent()) {
            PathVariable pathVariable = optionalPathVariable.get();
            ParamAnnotationValue paramAnnotationValue = ParamAnnotationValue.from(pathVariable);

            return new RequestParameterConverter(pathVariable.annotationType(), pathParams);
        }

        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, RequestBody.class);
        if (optionalRequestBody.isPresent()) {
            return new RequestBodyParameterConverter(requestBodyContent);
        }

        throw new RuntimeException("Does not exist match parameter converter.");
    }
}
