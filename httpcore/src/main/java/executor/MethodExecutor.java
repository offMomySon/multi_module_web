package executor;

import beanContainer.BeanContainer;
import java.lang.reflect.Method;
import variableExtractor.MethodConverter;

public class MethodExecutor {
    private final BeanContainer container;
    private final MethodConverter methodConverter;

    public MethodExecutor(BeanContainer container, MethodConverter methodConverter) {
        this.container = container;
        this.methodConverter = methodConverter;
    }

    public Object execute(Method javaMethod) {
        Class<?> declaringClass = javaMethod.getDeclaringClass();
        Object object = container.get(declaringClass);
        Object[] paramValues = methodConverter.convertAsParameterValues(javaMethod);

        return doExecute(javaMethod, object, paramValues);
    }

    private static Object doExecute(Method javaMethod, Object object, Object[] paramsValues) {
        try {
            return javaMethod.invoke(object, paramsValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
