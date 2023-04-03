package variableExtractor;

import java.lang.reflect.Method;
import java.util.Map;
import vo.RequestBodyContent;
import vo.RequestParameters;

public class Main {

    public static void main(String[] args) {
        String body = "{\"tst\": \"v\"}";
        Map<String, String> formParams = Map.of("kq1", "vq1", "kq2", "vq2");
        Map<String, String> pathVariables = Map.of("k1", "v1", "k2", "v2");

        ParameterConverterFactory parameterConverterFactory = new ParameterConverterFactory(new RequestParameters(formParams), new RequestParameters(pathVariables), RequestBodyContent.empty());

        Method method = getMethod();
        MethodParamValueExtractor methodParamValueExtractor = new MethodParamValueExtractor(parameterConverterFactory, method);
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
