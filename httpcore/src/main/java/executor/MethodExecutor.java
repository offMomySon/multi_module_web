package executor;

import beanContainer.BeanContainer;
import java.lang.reflect.Method;
import java.util.Objects;
import variableExtractor.MethodConverter;

public class MethodExecutor {
    private final BeanContainer container;
    private final MethodConverter methodConverter;

    public MethodExecutor(BeanContainer container, MethodConverter methodConverter) {
        this.container = container;
        this.methodConverter = methodConverter;
    }

    public Object execute(Method javaMethod) {
        Objects.requireNonNull(javaMethod, "javaMethod is null.");

        Class<?> declaringClass = javaMethod.getDeclaringClass();

        Object instance = container.get(declaringClass);
        Object[] values = methodConverter.convertAsParameterValues(javaMethod);

        return doExecute(instance, javaMethod, values);
    }

    private static Object doExecute(Object object, Method javaMethod, Object[] paramsValues) {
        try {
            return javaMethod.invoke(object, paramsValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
