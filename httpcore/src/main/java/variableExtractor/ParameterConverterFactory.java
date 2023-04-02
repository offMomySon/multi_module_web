package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import mapper.AnnotationUtils;
import marker.PathVariable;
import marker.RequestBody;
import marker.RequestParam;
import vo.RequestBodyContent;
import vo.RequestParameters;


public class ParameterConverterFactory {
    private final RequestParameters formParams;
    private final RequestParameters pathParams;
    private final RequestBodyContent requestBodyContent;

    public ParameterConverterFactory(RequestParameters formParams, RequestParameters pathParams, RequestBodyContent requestBodyContent) {
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
            return new RequestParameterConverter(formParams);
        }

        Optional<PathVariable> optionalPathVariable = AnnotationUtils.find(parameter, PathVariable.class);
        if (optionalPathVariable.isPresent()) {
            return new PathVariableParameterConverter(pathParams);
        }

        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, RequestBody.class);
        if (optionalRequestBody.isPresent()) {
            return new RequestBodyParameterConverter(requestBodyContent);
        }

        throw new RuntimeException("Does not exist match parameter converter.");
    }
}
