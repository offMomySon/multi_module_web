package variableExtractor;

import java.lang.reflect.Method;
import java.util.Map;
import vo.ParamAnnotationValue;
import vo.RequestBodyContent;
import vo.RequestParameters;

public class Main {

    public static void main(String[] args) {
        String body = "{\"tst\": \"v\"}";
        Map<String, String> pathVariables = Map.of("k1", "v1", "k2", "v2");
        Map<String, String> queryParams = Map.of("kq1", "vq1", "kq2", "vq2");

        PathVariableParameterConverter pathVariableParamValueExtractor = new PathVariableParameterConverter(new RequestParameters(pathVariables));
        RequestParameterConverter requestParamValueExtractor = new RequestParameterConverter(new RequestParameters(queryParams), new ParamAnnotationValue("k1", true, "defaultValue"));
        RequestBodyParameterConverter requestBodyParamValueExtractor = new RequestBodyParameterConverter(new RequestBodyContent(body));

        LastParameterConverter lastParamValueExtractor = new LastParameterConverter(requestBodyParamValueExtractor);
        ChainParameterConverter postChainParamValueExtractor = new ChainParameterConverter(requestParamValueExtractor, lastParamValueExtractor);
        ChainParameterConverter chainParamValueExtractor = new ChainParameterConverter(pathVariableParamValueExtractor, postChainParamValueExtractor);

        Method method = getMethod();
        MethodParamValueExtractor methodParamValueExtractor = new MethodParamValueExtractor(chainParamValueExtractor, method);
        Object[] objects = methodParamValueExtractor.extractValues();

    }

    public void test() {

    }

    public static Method getMethod() {
        try {
            return Main.class.getMethod("test");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
